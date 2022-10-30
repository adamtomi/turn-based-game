package com.vamk.tbg.game;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.util.RandomUtil;
import com.vamk.tbg.util.LogUtil;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game implements AutoCloseable {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
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
        // TODO read i from config
        // TODO read maxHealth from config
        for (int i = 0; i < 3; i++) {
            this.friends.add(new Entity(nextId++, false, 1000));
            this.hostiles.add(new Entity(nextId++, true, 1000));
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
                maybePromptAndPlay(entity);

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

    private void maybePromptAndPlay(Entity entity) {
        int move = -1;
        Entity target;
        if (!entity.isHostile()) {
            while (move == -1) move = promptUser();
            target = readEntity();
        } else {
            move = this.random.nextInt(2) + 1; // Add 1 so that we never hit 0
            target = RandomUtil.pickRandom(this.friends);
        }

        entity.tick();
        play(entity, target, move);
    }

    private void play(Entity entity, Entity target, int move) {
        LOGGER.info("Entity %d makes this move: %d".formatted(entity.getId(), move));
        // FROZEN rids the entitiy from this round
        if (entity.hasEffect(StatusEffect.FROZEN)) return;

        if (move == 1) entity.getFriendlyMove().perform(entity, target);
        else entity.getHostileMove().perform(entity, target);

        // CAFFEINATED grants another turn
        if (entity.hasEffect(StatusEffect.CAFFEINATED)) maybePromptAndPlay(entity);
    }

    // TODO refactor prompUser (maybe move to a different class?)
    private int promptUser() {
        try {
            LOGGER.info("Make your move, my friend! (Select an option from below)");
            LOGGER.info("1) Apply BUFFS to yourself | 2) Damage your enemy");

            int move = this.scanner.nextInt();
            if (move == 1 || move == 2) {
                return move;

            } else {
                LOGGER.warning("%d is an invalid option, try again".formatted(move));
            }
        } catch (InputMismatchException ex) {
            LOGGER.warning("You provided invalid input, try again");
        }

        return -1;
    }

    // TODO refactor this crap
    private Entity readEntity() {
        String options = this.hostiles.stream().filter(entity -> !entity.isDead())
                .map(Entity::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        while (true) {
            try {
                LOGGER.info("Which entity do you want to damage?");
                LOGGER.info(options);

                int entityId = this.scanner.nextInt();
                Optional<Entity> target = this.hostiles.stream().filter(entity -> entity.getId() == entityId)
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
