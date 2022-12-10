package com.vamk.tbg.game;

import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EffectsUpdatedSignal;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityHealthChangedSignal;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.Tickable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Entity implements Tickable {
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

    public int getId() {
        return this.id;
    }

    public boolean isHostile() {
        return this.hostile;
    }

    /**
     * Determines whether this entity is an enemy of
     * "that" entity. This method takes the CONFUSED
     * status effect into consideration.
     *
     * NOTE: This relationship is not bidirectional.
     * This entity might be hostile towards "that"
     * entity, but that doesn't mean it's true
     * the other way around.
     *
     * @see StatusEffect#CONFUSED
     * @return true if this entity can attack "that" entity
     */
    public boolean isEnemyOf(Entity that) {
        return hasEffect(StatusEffect.CONFUSED) == (this.hostile == that.hostile);
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public int getHealth() {
        return this.health;
    }

    public void damage(int dmg) {
        updateHealth(Math.max(0, this.health - dmg));
        if (this.health <= 0) this.dispatcher.dispatch(new EntityDeathSignal(this));
    }

    public void heal(int hp) {
        updateHealth(Math.min(this.health + hp, this.maxHealth));
    }

    public void heal() {
        updateHealth(this.maxHealth);
    }

    /**
     * This function is used by all public functions
     * that modify the entity's health. The reason for
     * that is that this will not only change the health
     * to the new value, but once that's done, it'll fire
     * a {@link EntityHealthChangedSignal} signal, so that
     * listeners (mainly UI related) can perform
     * necessary actions.
     */
    private void updateHealth(int health) {
        int previous = this.health;
        this.health = health;
        this.dispatcher.dispatch(new EntityHealthChangedSignal(this, previous));
    }

    public boolean isDead() {
        return this.health <= 0;
    }

    public List<Move> getMoves() {
        return List.copyOf(this.moves);
    }

    public void applyEffect(StatusEffect effect) {
        this.activeEffects.merge(effect, effect.getRounds(), (key, value) -> Math.min(value + 1, StatusEffect.MAX_ROUNDS));
        this.dispatcher.dispatch(new EffectsUpdatedSignal(this));
    }

    public boolean hasEffect(StatusEffect effect) {
        return this.activeEffects.containsKey(effect);
    }

    public Map<StatusEffect, Integer> getEffects() {
        return Map.copyOf(this.activeEffects);
    }

    public void removeEffect(StatusEffect effect) {
        this.activeEffects.remove(effect);
        this.dispatcher.dispatch(new EffectsUpdatedSignal(this));;
    }

    public void cure() {
        Set<StatusEffect> negativeEffects = this.activeEffects.keySet()
                .stream()
                .filter(StatusEffect::isHarmful)
                .collect(Collectors.toSet());

        negativeEffects.forEach(this.activeEffects::remove);
        this.dispatcher.dispatch(new EffectsUpdatedSignal(this));
    }

    public EntitySnapshot createSnapshot() {
        List<String> moves = this.moves.stream().map(Move::getId).toList();
        return new EntitySnapshot(this.id, this.hostile, this.health, this.maxHealth, moves);
    }

    @Override
    public void tick() {
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

        Entity create(boolean hostile, int maxHeath, List<Move> moves) {
            Entity entity =  new Entity(this.nextId.getAndIncrement(), hostile, maxHeath, moves, this.dispatcher);
            LOGGER.info("Created entity %s".formatted(entity));
            return entity;
        }

        Entity create(EntitySnapshot snapshot) {
            List<Move> moves = snapshot.moves().stream()
                    .map(this.moves::get)
                    .toList();
            Entity entity =  new Entity(snapshot.id(), snapshot.hostile(), snapshot.health(), snapshot.maxHealth(), moves, this.dispatcher);
            LOGGER.info("Restored entity %s".formatted(entity));
            return entity;
        }
    }
}
