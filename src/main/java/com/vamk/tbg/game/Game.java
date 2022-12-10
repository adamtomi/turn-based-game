package com.vamk.tbg.game;

import com.vamk.tbg.combat.BuffMove;
import com.vamk.tbg.combat.CureMove;
import com.vamk.tbg.combat.DamageMove;
import com.vamk.tbg.combat.DebuffMove;
import com.vamk.tbg.combat.HealAllMove;
import com.vamk.tbg.combat.HealMove;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.combat.SplashDamageMove;
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
import com.vamk.tbg.signal.impl.UserReadySignal;
import com.vamk.tbg.util.Cursor;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {
    private static final Logger LOGGER = LogUtil.getLogger(Game.class);
    private final Map<String, Move> moves;
    private final SignalDispatcher dispatcher;
    private final Config config;
    private final Entity.Factory entityFactory;
    private final List<Entity> entities;
    private final Set<StatusEffectHandler> effectHandlers;
    private Cursor<Entity> cursor;
    private boolean importedState = false;

    public Game(SignalDispatcher dispatcher, Config config) {
        // Load all moves into a map
        this.moves = Stream.of(
                new BuffMove(config),
                new CureMove(),
                new DebuffMove(config),
                new DamageMove(config),
                new HealAllMove(config),
                new HealMove(config),
                new SplashDamageMove(config)
        ).collect(Collectors.toMap(Move::getId, Function.identity()));

        this.dispatcher = dispatcher;
        this.config = config;
        this.entityFactory = new Entity.Factory(dispatcher, this.moves);
        this.entities = new ArrayList<>();
        this.effectHandlers = Set.of(new BleedingEffectHandler(config), new RegenEffectHandler(config));
    }

    public GameState exportState() {
        List<EntitySnapshot> entities = this.entities.stream().map(Entity::createSnapshot).toList();
        return new GameState(entities, this.cursor.getInternalCursor());
    }

    public void importState(GameState state) {
        LOGGER.info("Restoring previous game state...");
        List<Entity> entities = state.entities().stream().map(this.entityFactory::create).toList();
        this.entities.addAll(entities);
        this.cursor = new Cursor<>(this.entities, state.cursor());
        this.cursor.rollback();
        this.importedState = true;
    }

    public void launch() {
        prepare();
        gameLoop();
        LOGGER.info("Shutting down, thank you :)");
    }

    private void prepare() {
        this.dispatcher.subscribe(EntityDeathSignal.class, this::onEntityDeath);
        /*
         * If a previously exported game state was restored, we don't
         * need to ge through this setup process.
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

        return result;
    }

    private void onEntityDeath(EntityDeathSignal signal) {
        Entity dead = signal.getEntity();
        LOGGER.info("Entity %d died, removing it from the board...".formatted(dead.getId()));
        this.entities.remove(dead);
    }
    
    private void gameLoop() {
        while (shouldContinue()) {
            Entity entity = this.cursor.advance();
            LOGGER.info("Entity %d is playing".formatted(entity.getId()));
            play(entity);
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
                move = entity.getMoves().get(signal.getMoveIndex());
                target = signal.getTarget();
            } else {
                move = RandomUtil.pickRandom(entity.getMoves());
                List<Entity> targets = this.entities.stream()
                        .filter(x -> !x.isDead())
                        .filter(x -> !move.isAttack() || !x.equals(entity)) // Do not let entity attack itself
                        .filter(x -> move.isAttack() == entity.isEnemyOf(x))
                        .toList();

                target = RandomUtil.pickRandom(targets);
            }

            MoveContext context = new MoveContext(entity, target, List.copyOf(this.entities));
            move.perform(context);
        }

        // CAFFEINATED grants another turn
        if (entity.hasEffect(StatusEffect.CAFFEINATED)) play(entity);
    }

    public List<Entity> getEntities() {
        return List.copyOf(this.entities);
    }
}
