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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game implements AutoCloseable {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final Scanner scanner = new Scanner(System.in);
    /* Stores all friendly entities */
    private final List<Entity> friends = new ArrayList<>();
    /* Stores all hostile entities */
    private final List<Entity> hostiles = new ArrayList<>();
    /*
     * This is list responsible for storing all entities
     * as well as determining the order in which these
     * entities get to play their turns.
     */
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
            this.friends.add(new Entity(nextId++, false, 1000, moves));
            this.hostiles.add(new Entity(nextId++, true, 1000, moves));
        }

        this.entities.addAll(this.friends);
        this.entities.addAll(this.hostiles);

        RandomUtil.randomize(this.entities);
        LOGGER.info("Done!");
    }

    private void gameLoop() {
        while (true) {
            LOGGER.info("=========== | Entering game cycle | ===========");
            // Use iterator instead of regular for loop, because it can remove
            // items from the list while iterating through it.
            for (Iterator<Entity> iter = this.entities.iterator(); iter.hasNext();) {
                Entity entity = iter.next();
                if (checkDeadEntity(entity)) {
                    iter.remove();
                    continue;
                }

                if (!shouldContinue()) return;

                LOGGER.info("----------- | Entity %d is playing | -----------".formatted(entity.getId()));
                play(entity);

                LOGGER.info("-----------------------------------------------");
            }

            cleanupDeadEntities();
            printEntities();
            LOGGER.info("===============================================");
        }
    }

    private boolean shouldContinue() {
        return !this.friends.isEmpty() && !this.hostiles.isEmpty();
    }

    private void play(Entity entity) {
        /*
         * Make sure to tick the entity before potentially
         * skipping their round.
         */
        entity.tick();
        // FROZEN rids the entity from this round
        if (entity.hasEffect(StatusEffect.FROZEN)) return;

        Move move;
        Entity target = null;
        boolean confused = entity.hasEffect(StatusEffect.CONFUSED);
        if (!entity.isHostile() && !confused) {
            move = readMove(entity);
            if (move.isTargeted()) target = readEntity(move);
        } else {
            move = RandomUtil.pickRandom(entity.getMoves());
            /*List<Entity> targets = move.isAttack()
                    ? (confused ? this.hostiles : this.friends)
                    : (confused ? this.friends : this.hostiles);*/
            List<Entity> targets = this.entities.stream()
                    .filter(x -> confused != x.isEnemyOf(entity))
                    .filter(x -> !x.isDead())
                    .toList();
            if (move.isTargeted()) target = RandomUtil.pickRandom(targets);
        }

        MoveContext context = new MoveContext(entity, target, this.entities);
        move.perform(context);

        // CAFFEINATED grants another turn
        if (entity.hasEffect(StatusEffect.CAFFEINATED)) play(entity);
    }

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

    // TODO refactor this crap
    private Entity readEntity(Move move) {
        List<Entity> potentialTargets = move.isAttack() ? this.hostiles : this.friends;
        String options = potentialTargets.stream().filter(entity -> !entity.isDead())
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

    private void printEntities() {
        LOGGER.info("== Current entity info ==");
        this.entities.forEach(entity -> LOGGER.info("ID: %d | Health: %d | Effects: %s | Hostile: %b"
                .formatted(entity.getId(), entity.getHealth(), entity.getEffects(), entity.isHostile())));
    }

    private boolean checkDeadEntity(Entity entity) {
        if (!entity.isDead()) return false;

        LOGGER.info("Entity %d died, removing it from the board...".formatted(entity.getId()));
        LOGGER.info("Hostile: %d | Friendly: %d".formatted(this.hostiles.size(), this.friends.size()));
        if (entity.isHostile()) this.hostiles.remove(entity);
        else this.friends.remove(entity);

        LOGGER.info("Hostile: %d | Friendly: %d".formatted(this.hostiles.size(), this.friends.size()));

        return true;
    }

    private void cleanupDeadEntities() {
        this.entities.removeIf(Entity::isDead);
        int friends = this.friends.size();
        this.friends.removeIf(Entity::isDead);
        int friendsDiff = friends - this.friends.size();
        if (friendsDiff > 0) LOGGER.info("Removed %d dead friendly entities".formatted(friendsDiff));

        int hostiles = this.hostiles.size();
        this.hostiles.removeIf(Entity::isDead);
        int hostilesDiff = hostiles - this.hostiles.size();
        if (hostilesDiff > 0) LOGGER.info("Removed %d dead hostile entities".formatted(hostilesDiff));
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
