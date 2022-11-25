package com.vamk.tbg.game;

import com.vamk.tbg.combat.BuffMove;
import com.vamk.tbg.combat.DebuffMove;
import com.vamk.tbg.combat.GenericAttackMove;
import com.vamk.tbg.combat.HealAllMove;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.effect.BleedingEffectHandler;
import com.vamk.tbg.effect.RegenEffectHandler;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.effect.StatusEffectHandler;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;
import com.vamk.tbg.ui.ButtonContainer;
import com.vamk.tbg.util.Cursor;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;
import com.vamk.tbg.util.UserInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Game {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final SignalDispatcher dispatcher;
    private final Config config;
    private final Entity.Factory entityFactory;
    private final List<Entity> entities;
    private final Set<StatusEffectHandler> effectHandlers;
    private Cursor<Entity> cursor;
    private boolean importedState = false;

    public Game(SignalDispatcher dispatcher, Config config) {
        this.dispatcher = dispatcher;
        this.config = config;
        this.entityFactory = new Entity.Factory(dispatcher);
        this.entities = new ArrayList<>();
        this.effectHandlers = Set.of(new BleedingEffectHandler(), new RegenEffectHandler());
    }

    public void launch() {
        prepare();
        gameLoop();
        LOGGER.info("Shutting down, thank you :)");
    }

    public boolean isFinished() {
        int hostiles = (int) this.entities.stream().filter(Entity::isHostile).count();
        return !(hostiles > 0 && this.entities.size() - hostiles > 0);
    }

    public GameState exportState() {
        List<EntitySnapshot> entities = this.entities.stream().map(Entity::createSnapshot).toList();
        return new GameState(entities, this.cursor.getInternalCursor());
    }

    public void importState(GameState state) {
        LOGGER.info("Restoring previous game state...");
        List<Entity> entities = state.getEntities().stream().map(this.entityFactory::create).toList();
        this.entities.addAll(entities);
        this.cursor = new Cursor<>(this.entities, state.getCursor());
        this.importedState = true;
    }

    private void prepare() {
        this.dispatcher.subscribe(EntityDeathSignal.class, this::onEntityDeath);
        /*
         * If a previously exported game state was restored, we don't
         * need to ge through this setup process.
         */
        if (!this.importedState) {
            LOGGER.info("Preparing game, spawning entities...");
            // TODO read moves from config (entity presets?)
            List<Move> moves = List.of(
                    new BuffMove(),
                    new HealAllMove(),
                    new DebuffMove(),
                    new GenericAttackMove()
            );

            int maxHealth = this.config.get(Keys.ENTITY_MAX_HEALTH);
            int entityCount = this.config.get(Keys.ENTITY_COUNT);

            for (int i = 0; i < entityCount; i++) {
                this.entities.add(this.entityFactory.create(false, maxHealth, moves));
                this.entities.add(this.entityFactory.create(true, maxHealth, moves));
            }

            RandomUtil.randomize(this.entities);
            this.cursor = new Cursor<>(this.entities);
        }

        this.dispatcher.dispatch(new GameReadySignal(this.entities));
        LOGGER.info("Done!");
    }

    private void onEntityDeath(EntityDeathSignal signal) {
        Entity dead = signal.getEntity();
        LOGGER.info("Entity %d died, removing it from the board...".formatted(dead.getId()));
        this.entities.remove(dead);
    }
    
    private void gameLoop() {
        while (!isFinished()) {
            Entity entity = this.cursor.advance();
            LOGGER.info("Entity %d is playing".formatted(entity.getId()));
            play(entity);
        }
    }

    private void play(Entity entity) {
        /*
         * Make sure to tick the entity before potentially
         * skipping their round.
         */
        entity.tick();
        this.effectHandlers.forEach(x -> x.applyTo(entity));
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
                UserInput input = ButtonContainer.getInstance().readUserInput();
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
