package com.vamk.tbg.game;

import com.vamk.tbg.combat.Move;
import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.di.qualifier.EffectHandlerSet;
import com.vamk.tbg.di.qualifier.MoveSet;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.effect.StatusEffectHandler;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;
import com.vamk.tbg.signal.impl.UserReadySignal;
import com.vamk.tbg.util.CollectionUtil;
import com.vamk.tbg.util.Cursor;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The main game class responsible for managing the gameloop
 * itself as well as creating all entities, dealing with
 * serialization and more.
 */
@Singleton
public class Game {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final SignalDispatcher dispatcher;
    private final Config config;
    private final Map<String, Move> moves;
    private final Set<StatusEffectHandler> effectHandlers;
    private final Entity.Factory entityFactory;
    private final List<Entity> entities;
    private Cursor<Entity> cursor;
    private boolean importedState = false;

    @Inject
    public Game(SignalDispatcher dispatcher,
                Config config,
                @MoveSet Set<Move> moves,
                @EffectHandlerSet Set<StatusEffectHandler> effectHandlers) {
        this.dispatcher = dispatcher;
        this.config = config;
        this.moves = CollectionUtil.mapOf(Move::getId, moves);
        this.effectHandlers = effectHandlers;
        this.entityFactory = new Entity.Factory(dispatcher, this.moves);
        this.entities = new ArrayList<>();
    }

    /**
     * Creates a serializable state of the current game
     * which can be used to continue the same game later
     * (assuming that the configuration doesn't change).
     *
     * @return The game state
     */
    public GameState exportState() {
        List<EntitySnapshot> entities = this.entities.stream().map(Entity::createSnapshot).toList();
        return new GameState(entities, this.cursor.getInternalCursor());
    }

    /**
     * Restores a previous game from the provided state.
     * It's assumed that the configuration wasn't touched
     * since this state was exported, otherwise things
     * might break.
     *
     * @param state The game state
     */
    public void importState(GameState state) {
        LOGGER.info("Restoring previous game state...");
        List<Entity> entities = state.entities().stream().map(this.entityFactory::create).toList();
        this.entities.addAll(entities);
        this.cursor = new Cursor<>(this.entities, state.cursor());
        this.cursor.rollback();
        this.importedState = true;
    }

    /**
     * Launches the game.
     *
     * @see this#prepare()
     * @see this#gameLoop()
     */
    public void launch() {
        prepare();
        gameLoop();
        LOGGER.info("Shutting down, thank you :)");
    }

    /**
     * Prepares this game. This method doesn't do much if
     * a previous game state was restored previously, since
     * in that case no entities need to be created. If that's
     * not the case however, this method will create all
     * entities for both teams (randomly selecting moves
     * for all of them).
     */
    private void prepare() {
        this.dispatcher.subscribe(EntityDeathSignal.class, this::onEntityDeath);
        /*
         * If a previously exported game state was restored, we don't
         * need to ge through the setup process.
         */
        if (!this.importedState) {
            LOGGER.info("Preparing game, spawning entities...");
            List<List<Move>> moveCombos = readAndValidateMoves();

            int maxHealth = this.config.get(Keys.ENTITY_MAX_HEALTH);
            int entityCount = this.config.get(Keys.ENTITY_COUNT);

            for (int i = 0; i < entityCount; i++) {
                this.entities.add(this.entityFactory.create(false, maxHealth, RandomUtil.pickRandom(moveCombos)));
                this.entities.add(this.entityFactory.create(true, maxHealth, RandomUtil.pickRandom(moveCombos)));
            }

            RandomUtil.randomize(this.entities);
            this.cursor = new Cursor<>(this.entities);
        }

        this.dispatcher.dispatch(new GameReadySignal(this.entities));
        LOGGER.info("Done!");
    }

    /**
     * Parses the configured move combinations. If all
     * of them end up being invalid (referring to a
     * non-existent move for instance, of specifying
     * an invalid amount of moves) an error is thrown.
     *
     * @return The parsed list of move combinations
     * @throws IllegalStateException If no valid combination was found
     */
    private List<List<Move>> readAndValidateMoves() {
        List<List<String>> configured = this.config.get(Keys.MOVE_PRESETS);
        int expectedCount = this.config.get(Keys.MOVE_COUNT);
        List<List<Move>> result = new ArrayList<>();

        for (List<String> preset : configured) {
            List<Move> moves = preset.stream()
                    .map(this.moves::get)
                    .filter(Objects::nonNull) // If the entry is null, then the specified ID was invalid
                    .toList();

            if (moves.size() != expectedCount) {
                LOGGER.warning("Detected invalid move preset: %s".formatted(preset));
            } else {
                result.add(moves);
            }
        }

        if (result.isEmpty()) throw new IllegalStateException("There are no valid move combinations");

        return result;
    }

    /**
     * Removes dead entities from {@link this#entities}. This
     * is needed since some commands rely on this list being
     * up-to-date.
     */
    private void onEntityDeath(EntityDeathSignal signal) {
        Entity dead = signal.getEntity();
        LOGGER.info("Entity %d died".formatted(dead.getId()));
        this.entities.remove(dead);
    }

    /**
     * This method runs the main gameloop. As long as
     * {@link this#shouldContinue()} returns true, the
     * next entity in the queue gets to play. Once said
     * method returns false, the game terminates.
     */
    private void gameLoop() {
        while (shouldContinue()) {
            Entity entity = this.cursor.advance();
            LOGGER.info("Entering game cycle, entity %d is playing".formatted(entity.getId()));
            play(entity, true);
        }
    }

    /**
     * A game should continue as long as there are entities
     * alive in both teams.
     *
     * @return Whether said condition is true
     */
    private boolean shouldContinue() {
        int hostiles = (int) this.entities.stream().filter(Entity::isHostile).count();
        return hostiles > 0 && this.entities.size() - hostiles > 0;
    }

    /**
     * Runs the main play logic for the game. If the
     * entity us controlled by the user (if the entity
     * is not hostile and not confused), the method
     * will block until there's user input, otherwise
     * a random move and target is selected.
     *
     * @param entity The currently playing entity
     * @param first Whether this is the first round
     *              for this entity in this game cycle
     *              (used for the caffeinated effect)
     * @see StatusEffect#CAFFEINATED
     * @see StatusEffect#CONFUSED
     * @see StatusEffect#FROZEN
     */
    private void play(Entity entity, boolean first) {
        /*
         * Make sure to update the entity before potentially
         * skipping its round.
         */
        entity.update();
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
                UserReadySignal signal = this.dispatcher.awaitSignal(UserReadySignal.class);
                move = signal.getMove();
                target = signal.getTarget();
            } else {
                move = RandomUtil.pickRandom(entity.getMoves());
                List<Entity> targets = this.entities.stream()
                        .filter(x -> !x.isDead())
                        .filter(x -> !move.isAttack() || !x.equals(entity)) // Do not let entity attack itself
                        .filter(x -> move.isAttack() == entity.isEnemyOf(x))
                        .toList();

                //TODO this is not the best way to go about this problem.
                // It'd be nice to have a better "AI" in place however,
                // in which case this will get replaced anyway.
                if (targets.isEmpty()) {
                    LOGGER.info("No suitable target found...");
                    return;
                }

                target = RandomUtil.pickRandom(targets);
            }

            MoveContext context = new MoveContext(entity, target, List.copyOf(this.entities));
            move.perform(context);
        }

        // CAFFEINATED grants another turn
        if (first && entity.hasEffect(StatusEffect.CAFFEINATED)) play(entity, false);
    }

    /**
     * Returns an immutable copy of the entity list.
     */
    public List<Entity> getEntities() {
        return List.copyOf(this.entities);
    }
}
