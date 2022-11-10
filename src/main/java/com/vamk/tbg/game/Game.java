package com.vamk.tbg.game;

import com.vamk.tbg.combat.BuffMove;
import com.vamk.tbg.combat.DebuffMove;
import com.vamk.tbg.combat.GenericAttackMove;
import com.vamk.tbg.combat.HealAllMove;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.util.RandomUtil;
import com.vamk.tbg.util.LogUtil;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final Scanner scanner = new Scanner(System.in);
    private final List<Entity> entities = new ArrayList<>();

    public void launch() {
        prepare();
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
            Entity target = null;
            if (!entity.isHostile() && !entity.hasEffect(StatusEffect.CONFUSED)) {
                move = readMove(entity);
                if (move.isTargeted()) target = readEntity(move);
            } else {
                move = RandomUtil.pickRandom(entity.getMoves());
                if (move.isTargeted()) {
                    List<Entity> targets = this.entities.stream()
                            .filter(x -> !x.isDead())
                            .filter(x -> !move.isAttack() || !x.equals(entity)) // Do not let entity to attack itself
                            .filter(x -> move.isAttack() == entity.isEnemyOf(x))
                            .toList();

                    target = RandomUtil.pickRandom(targets);
                }
            }

            MoveContext context = new MoveContext(entity, target, this.entities);
            move.perform(context);
        }

        // CAFFEINATED grants another turn
        if (entity.hasEffect(StatusEffect.CAFFEINATED)) play(entity);
    }

    // TODO This method will be removed later (once the GUI part is up and running)
    private Move readMove(Entity entity) {
        List<Move> moves = entity.getMoves();
        StringJoiner options = new StringJoiner(" ");
        for (int i = 0; i < moves.size(); i++) {
            options.add("%d)".formatted(i))
                    .add(moves.get(i).getId());
        }

        while (true) {
            try {
                LOGGER.info("Make your move, my friend! (Select an option from below)");
                LOGGER.info(options.toString());

                int move = this.scanner.nextInt();
                return entity.getMoves().get(move);
            } catch (IndexOutOfBoundsException | InputMismatchException ex) {
                LOGGER.warning("You provided invalid input, try again");
            }
        }
    }

    // TODO This method will be removed later (once the GUI part is up and running)
    private Entity readEntity(Move move) {
        List<Entity> potentialTargets = this.entities.stream()
                .filter(entity -> !entity.isDead())
                .filter(entity -> move.isAttack() == entity.isHostile())
                .toList();
        String options = potentialTargets.stream()
                .map(Entity::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        while (true) {
            try {
                LOGGER.info("Which entity do you want to perform your move on?");
                LOGGER.info(options);

                int entityId = this.scanner.nextInt();
                Optional<Entity> target = potentialTargets.stream().filter(entity -> entity.getId() == entityId)
                        .findFirst();
                if (target.isEmpty()) {
                    LOGGER.warning("There's no entity with id %d".formatted(entityId));
                    continue;
                }

                return target.orElseThrow();
            } catch (InputMismatchException ex) {
                LOGGER.warning("You provided invalid input, try again");
            }
        }
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
        if (diff > 0) LOGGER.info("Removed %d dead entities from the board");
    }

    public void destroy() {
        this.scanner.close();
    }
}
