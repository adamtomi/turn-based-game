package com.vamk.tbg.game;

import com.vamk.tbg.combat.BuffMove;
import com.vamk.tbg.combat.DebuffMove;
import com.vamk.tbg.combat.GenericAttackMove;
import com.vamk.tbg.combat.HealAllMove;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.ui.ButtonContainer;
import com.vamk.tbg.util.RandomUtil;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.UserInput;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Game {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final List<Entity> entities;

    public Game() {
        this.entities = new ArrayList<>();
    }

    public void launch() {
        prepare();
        ButtonContainer.getInstance().init();
        gameLoop();
        LOGGER.info("Shutting down, thank you :)");
    }

    private void prepare() {
        LOGGER.info("Preparing game, spawning entities...");
        int nextId = 0;
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
            this.entities.add(new Entity(nextId++, false, 1000, moves));
            this.entities.add(new Entity(nextId++, true, 1000, moves));
        }

        RandomUtil.randomize(this.entities);
        LOGGER.info("Done!");
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
                cleanupDeadEntities();
                LOGGER.info("-----------------------------------------------");
            }

            printEntities();
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
            if (!entity.isHostile() && !entity.hasEffect(StatusEffect.CONFUSED)) {
                ButtonContainer.getInstance().enableMoveButtons();
                ButtonContainer.getInstance().updateButtonsFor(entity);
                UserInput input = ButtonContainer.getInstance().readUserInput();
                move = entity.getMoves().get(input.moveIndex());
                target = input.target();
                ButtonContainer.getInstance().disableMoveButtons();
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

    // TODO This method will be removed later (once the GUI part is up and running)
    private void printEntities() {
        LOGGER.info("== Current entity info ==");
        this.entities.forEach(entity -> LOGGER.info("ID: %d | Health: %d | Effects: %s | Hostile: %b"
                .formatted(entity.getId(), entity.getHealth(), entity.getEffects(), entity.isHostile())));
    }

    private void cleanupDeadEntities() {
        int entities = this.entities.size();
        this.entities.removeIf(Entity::isDead);
        int diff = entities - this.entities.size();
        if (diff > 0) LOGGER.info("Removed %d dead entities from the board".formatted(diff));
    }

    public List<Entity> getEntities() {
        return List.copyOf(this.entities);
    }
}
