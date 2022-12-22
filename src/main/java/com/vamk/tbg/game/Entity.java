package com.vamk.tbg.game;

import com.vamk.tbg.game.combat.Move;
import com.vamk.tbg.game.effect.StatusEffect;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EffectsUpdatedSignal;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityHealthChangedSignal;
import com.vamk.tbg.util.LogUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Entity {
    private final Map<StatusEffect, Integer> activeEffects;
    private final int id;
    private final boolean hostile;
    private final int maxHealth;
    private int health;
    private final List<Move> moves;
    private final SignalDispatcher dispatcher;

    public Entity(int id, boolean hostile, int health, int maxHealth, List<Move> moves, SignalDispatcher dispatcher) {
        this.id = id;
        this.hostile = hostile;
        this.health = health;
        this.maxHealth = maxHealth;
        this.moves = moves;
        this.dispatcher = dispatcher;
        this.activeEffects = new HashMap<>();
    }

    public Entity(int id, boolean hostile, int maxHealth, List<Move> moves, SignalDispatcher dispatcher) {
        this(id, hostile, maxHealth, maxHealth, moves, dispatcher);
    }

    /**
     * Returns the (unique) ID of this entity.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns whether this entity is hostile.
     * Entities are considered hostile if they
     * naturally attack the user (a non-hostile
     * entity still could do that under the influence
     * of the {@link StatusEffect#CONFUSED} effect,
     * but the entity wouldn't be considered hostile
     * because of that).
     */
    public boolean isHostile() {
        return this.hostile;
    }

    /**
     * Determines whether this entity is an enemy of
     * "that" entity. This method takes the CONFUSED
     * status effect into consideration.
     * NOTE: This relationship is not bidirectional.
     * This entity might be an enemy of "that"
     * entity, but that doesn't mean it's true
     * the other way around.
     *
     * @see StatusEffect#CONFUSED
     * @return true if this entity can attack "that" entity
     */
    public boolean isEnemyOf(Entity that) {
        return hasEffect(StatusEffect.CONFUSED) == (this.hostile == that.hostile) && !that.equals(this);
    }

    /**
     * Returns the max health (which is the starting
     * hp as well) of this entity.
     */
    public int getMaxHealth() {
        return this.maxHealth;
    }

    /**
     * Returns the current health of this entity.
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Damages the entity. If the health goes down
     * to (or below) 0, the entity dies (in which case
     * an {@link EntityDeathSignal} is dispatched)
     *
     * @param dmg The amount of damage
     * @see this#updateHealth(int)
     */
    public void damage(int dmg) {
        updateHealth(Math.max(0, this.health - dmg));
        if (this.health <= 0) this.dispatcher.dispatch(new EntityDeathSignal(this));
    }

    /**
     * Heals the entity. Note: the current health
     * cannot go beyond the max health.
     *
     * @param hp The amount of health
     * @see this#updateHealth(int)
     */
    public void heal(int hp) {
        updateHealth(Math.min(this.health + hp, this.maxHealth));
    }

    /**
     * Fully heals the entity (practically
     * sets the health back to the max health).
     *
     * @see this#updateHealth(int)
     */
    public void heal() {
        updateHealth(this.maxHealth);
    }

    /**
     * This method is used by all public functions
     * that modify the entity's health. The reason for
     * that is that this will not only change the health
     * to the new value, but once that's done, it'll fire
     * a {@link EntityHealthChangedSignal} signal, so that
     * listeners (mainly UI related) can perform
     * necessary actions.
     *
     * @see EntityHealthChangedSignal
     */
    private void updateHealth(int health) {
        int previous = this.health;
        this.health = health;
        this.dispatcher.dispatch(new EntityHealthChangedSignal(this, previous));
    }

    /**
     * Returns whether the entity is dead (that is
     * the current health is below or equal to 0).
     */
    public boolean isDead() {
        return this.health <= 0;
    }

    /**
     * Returns an immutable copy of {@link this#moves}
     */
    public List<Move> getMoves() {
        return List.copyOf(this.moves);
    }

    /**
     * Applies an effect to this entity. If the effect
     * is not yet applied, the default {@link StatusEffect#getRounds()}
     * is used for the number of rounds. Otherwise, the
     * current is increased by one. After that, a new
     * {@link EffectsUpdatedSignal} is dispatched.
     *
     * @param effect The effect to apply to this entity
     * @see EffectsUpdatedSignal
     */
    public void applyEffect(StatusEffect effect) {
        this.activeEffects.merge(effect, effect.getRounds(), (key, value) -> Math.min(value + 1, StatusEffect.MAX_ROUNDS));
        this.dispatcher.dispatch(new EffectsUpdatedSignal(this));
    }

    /**
     * Returns whether the effect is applied to this
     * entity.
     *
     * @param effect The effect
     */
    public boolean hasEffect(StatusEffect effect) {
        return this.activeEffects.containsKey(effect);
    }

    /**
     * Returns an immutable copy of {@link this#activeEffects}.
     */
    public Map<StatusEffect, Integer> getEffects() {
        return Map.copyOf(this.activeEffects);
    }

    /**
     * Remove an effect from this entity. At the same
     * time, a new {@link EffectsUpdatedSignal} is
     * dispatched.
     *
     * @param effect StatusEffect The effect
     * @see this#cure()
     * @see this#update()
     * @see EffectsUpdatedSignal
     */
    public void removeEffect(StatusEffect effect) {
        this.activeEffects.remove(effect);
        this.dispatcher.dispatch(new EffectsUpdatedSignal(this));
    }

    /**
     * Removes all negative status effects from this entity.
     *
     * @see this#removeEffect(StatusEffect)
     * @see com.vamk.tbg.game.combat.CureMove
     */
    public void cure() {
        Set<StatusEffect> negativeEffects = this.activeEffects.keySet()
                .stream()
                .filter(StatusEffect::isHarmful)
                .collect(Collectors.toSet());

        negativeEffects.forEach(this::removeEffect);
    }

    /**
     * Creates a serializable snapshot of this entity
     * which only contains the necessary information
     * about this entity.
     *
     * @return The created snapshot
     */
    public EntitySnapshot createSnapshot() {
        List<String> moves = this.moves.stream().map(Move::getId).toList();
        return new EntitySnapshot(this.id, this.hostile, this.health, this.maxHealth, moves);
    }

    /**
     * Every time this entity plays, the number of rounds for
     * each effect will decrease by one. Expired effects will
     * be removed.
     *
     * @see this#removeEffect(StatusEffect)
     */
    public void update() {
        Set<StatusEffect> expired = new HashSet<>();
        for (Map.Entry<StatusEffect, Integer> entry : this.activeEffects.entrySet()) {
            int rounds = entry.getValue() - 1;

            if (rounds < 0) expired.add(entry.getKey());
            else entry.setValue(rounds);
        }

        expired.forEach(this::removeEffect);
    }

    @Override
    public String toString() {
        return "Entity { id=%d, hostile=%b, health=%d }".formatted(this.id, this.hostile, this.health);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Entity that) {
            return this.id == that.id;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 31 * this.id;
    }

    /**
     * A simple factory class for entities to take away
     * all the hassle of managing IDs and restoring
     * entities from snapshots.
     */
    static final class Factory {
        private static final Logger LOGGER = LogUtil.getLogger(Factory.class);
        private final SignalDispatcher dispatcher;
        private final Map<String, Move> moves;
        private final AtomicInteger nextId;

        Factory(SignalDispatcher dispatcher, Map<String, Move> moves) {
            this.dispatcher = dispatcher;
            this.moves = moves;
            this.nextId = new AtomicInteger(0);
        }

        /**
         * This is the normal way to create an entity.
         *
         * @param hostile Whether the entity should be hostile
         * @param maxHealth The max health of the entity
         * @param moves The list of moves this entity can peform
         * @return The newly created entity
         */
        Entity create(boolean hostile, int maxHealth, List<Move> moves) {
            Entity entity =  new Entity(this.nextId.getAndIncrement(), hostile, maxHealth, moves, this.dispatcher);
            LOGGER.info("Created entity %s".formatted(entity));
            return entity;
        }

        /**
         * This method is responsible for creating an entity
         * based on a snapshot. Snapshots can be loaded from
         * previous game states.
         *
         * @param snapshot The snapshot to restore the entity from
         * @return the newly created entity
         * @see Game#importState(GameState)
         * @see EntitySnapshot
         */
        Entity create(EntitySnapshot snapshot) {
            List<Move> moves = snapshot.moves().stream()
                    .map(this.moves::get)
                    .toList();
            Entity entity = new Entity(snapshot.id(), snapshot.hostile(), snapshot.health(), snapshot.maxHealth(), moves, this.dispatcher);
            LOGGER.info("Restored entity %s".formatted(entity));
            return entity;
        }
    }
}
