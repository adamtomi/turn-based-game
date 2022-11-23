package com.vamk.tbg.game;

import com.vamk.tbg.combat.BuffMove;
import com.vamk.tbg.combat.DebuffMove;
import com.vamk.tbg.combat.GenericAttackMove;
import com.vamk.tbg.combat.HealAllMove;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;
import com.vamk.tbg.ui.GameContainer;
import com.vamk.tbg.util.RandomUtil;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.UserInput;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Game {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final SignalDispatcher dispatcher;
    private final Entity.Factory entityFactory;
    private final List<Entity> entities;

    public Game(SignalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.entityFactory = new Entity.Factory(dispatcher);
        this.entities = new ArrayList<>();
    }

    public void launch() {
        prepare();
        gameLoop();
        LOGGER.info("Shutting down, thank you :)");
    }

    private void prepare() {
        LOGGER.info("Preparing game, spawning entities...");
        this.dispatcher.subscribe(EntityDeathSignal.class, this::onEntityDeath);
        // TODO read moves from config (entity presets?)
        List<Move> moves = List.of(
                new BuffMove(),
                new HealAllMove(),
                new DebuffMove(),
                new GenericAttackMove()
        );

        // TODO read i from config
        // TODO read maxHealth from config
        for (int i = 0; i < 3; i++) {
            this.entities.add(this.entityFactory.create(false, 1000, moves));
            this.entities.add(this.entityFactory.create(true, 1000, moves));
        }

        RandomUtil.randomize(this.entities);
        this.dispatcher.dispatch(new GameReadySignal(this.entities));
        LOGGER.info("Done!");
    }

    private void onEntityDeath(EntityDeathSignal signal) {
        Entity dead = signal.getEntity();
        LOGGER.info("Entity %d died, removing it from the board...".formatted(dead.getId()));
        this.entities.remove(dead);
    }
    
    private void gameLoop() {
        while (true) {
            LOGGER.info("=========== | Entering game cycle | ===========");
            /*
             * Don't use Java's enhanced for loop, since this#cleanupDeadEntities
             * might remove elements from the list while the iteration is happening,
             * which would lead to concurrent modifications.
             */
            for (int i = 0; i < this.entities.size(); i++) {
                if (!shouldContinue()) return;

                Entity entity = this.entities.get(i);
                LOGGER.info("----------- | Entity %d is playing | -----------".formatted(entity.getId()));
                play(entity);
                LOGGER.info("-----------------------------------------------");
            }

            LOGGER.info("===============================================");
        }
    }

    private boolean shouldContinue() {
        int hostiles = (int) this.entities.stream().filter(Entity::isHostile).count();
        return hostiles > 0 && this.entities.size() - hostiles > 0;
    }

    private void play(Entity entity) {
        /*
         * Make sure to tick the entity before potentially
         * skipping their round.
         */
        entity.tick();
        // FROZEN rids the entity from this round
        if (!entity.hasEffect(StatusEffect.FROZEN)) {
            Move move;
            Entity target;
            /*
             * User can control this entity, if it's not confused (if it is, the computer will take over)
             * and it's not hostile.
             */
            boolean userControlled = !entity.isHostile() && !entity.hasEffect(StatusEffect.CONFUSED);
            this.dispatcher.dispatch(new EntityPlaysSignal(entity, userControlled));

            if (userControlled) {
                UserInput input = GameContainer.getInstance().readUserInput();
                move = entity.getMoves().get(input.moveIndex());
                target = input.target();
            } else {
                move = RandomUtil.pickRandom(entity.getMoves());
                List<Entity> targets = this.entities.stream()
                        .filter(x -> !x.isDead())
                        .filter(x -> !move.isAttack() || !x.equals(entity)) // Do not let entity attack itself
                        .filter(x -> move.isAttack() == entity.isEnemyOf(x))
                        .toList();

                target = RandomUtil.pickRandom(targets);
            }

            MoveContext context = new MoveContext(entity, target, this.entities);
            move.perform(context);
        }

        // CAFFEINATED grants another turn
        if (entity.hasEffect(StatusEffect.CAFFEINATED)) play(entity);
    }
}
