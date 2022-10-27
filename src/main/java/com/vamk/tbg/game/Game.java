package com.vamk.tbg.game;

import com.vamk.tbg.effect.BleedingEffectHandler;
import com.vamk.tbg.effect.RegenEffectHandler;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.effect.StatusEffectHandler;
import com.vamk.tbg.util.LogUtil;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game implements AutoCloseable {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();
    private final List<Entity> entities = new ArrayList<>();
    private final List<StatusEffectHandler> effectHandlers = List.of(
            new BleedingEffectHandler(this),
            new RegenEffectHandler(this)
    );

    public List<Entity> getEntities() {
        return List.copyOf(this.entities);
    }

    public void launch() {
        prepare();
        gameLoop();
        close();
        LOGGER.info("Shutting down, thank you :)");
    }

    private void prepare() {
        // TODO change how entities are added...
        LOGGER.info("Preparing game, spawning entities...");
        this.entities.add(new Entity(
                0,
                false,
                1000
        ));
        this.entities.add(new Entity(
                1,
                true,
                1000
        ));

        LOGGER.info("Done!");
    }

    private void gameLoop() {
        while (shouldContinue()) {
            LOGGER.info("----------- | Entering game cycle | -----------");
            for (Entity entity : this.entities) {
                maybePromptAndPlay(entity);
            }

            this.effectHandlers.forEach(StatusEffectHandler::tick);

            /*
             * Despawn dead entities. The reason it's not done earlier
             * is that some status effects (bleeding in particular) might
             * end up killing entities if they're on low health before.
             */
            int removed = cleanupEntities();
            if (removed > 0) LOGGER.info("Removed %d dead entities from the board".formatted(removed));

            printEntities();
            LOGGER.info("-----------------------------------------------");
        }
    }

    private boolean shouldContinue() {
        int friends = (int) this.entities.stream().filter(entity -> !entity.isHostile()).count();
        int hostiles = Math.max(0, this.entities.size() - friends);
        return friends > 0 && hostiles > 0;
    }

    private void maybePromptAndPlay(Entity entity) {
        int move;
        if (!entity.isHostile()) {
            move = promptUser();
            while (move == -1) move = promptUser();
        } else {
            move = this.random.nextInt(2) + 1; // Add 1 so that we never hit 0
        }

        entity.tick();
        play(entity, move);
    }

    private void play(Entity entity, int move) {
        LOGGER.info("Entity %d makes this move: %d".formatted(entity.getId(), move));
        if (entity.hasEffect(StatusEffect.FROZEN)) return;

        // TODO Should change how entities are selected...
        if (move == 1) entity.getFriendlyMove().perform(entity, this.entities.get(entity.isHostile() ? 1 : 0));
        else entity.getHostileMove().perform(entity, this.entities.get(entity.isHostile() ? 0 : 1));

        // Caffeinated grants another turn
        if (entity.hasEffect(StatusEffect.CAFFEINATED)) maybePromptAndPlay(entity);
    }

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

    private int cleanupEntities() {
        Set<Entity> dead = this.entities.stream()
                .filter(Entity::isDead)
                .collect(Collectors.toSet());
        dead.forEach(this.entities::remove);
        return dead.size();
    }

    private void printEntities() {
        LOGGER.info("== Current entity info ==");
        this.entities.forEach(entity -> LOGGER.info("ID: %d | Health: %d | Effects: %s"
                .formatted(entity.getId(), entity.getHealth(), entity.getEffects())));
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
