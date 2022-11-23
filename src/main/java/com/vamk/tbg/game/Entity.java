package com.vamk.tbg.game;

import com.vamk.tbg.Constants;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.BleedingEffectHandler;
import com.vamk.tbg.effect.RegenEffectHandler;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.Tickable;
import com.vamk.tbg.util.Watchable;

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
    private final Set<Tickable> tickables;
    private final int id;
    private final boolean hostile;
    private final int maxHealth;
    private final Watchable<Integer> health;
    private final List<Move> moves;
    private final SignalDispatcher dispatcher;

    public Entity(int id, boolean hostile, int maxHealth, List<Move> moves, SignalDispatcher dispatcher) {
        this.id = id;
        this.hostile = hostile;
        this.maxHealth = maxHealth;
        this.health = new Watchable<>(maxHealth);
        this.moves = moves;
        this.dispatcher = dispatcher;

        this.activeEffects = new HashMap<>();
        this.tickables = Set.of(
                new BleedingEffectHandler(this),
                new RegenEffectHandler(this)
        );
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
     * NOTE: This relationship is not bi-directional.
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

    public Watchable<Integer> getHealth() {
        return this.health;
    }

    public void damage(int dmg) {
        this.health.set(Math.max(0, this.health.get() - dmg));
    }

    public void heal(int hp) {
        this.health.set(Math.min(this.health.get() + hp, this.maxHealth));
    }

    public void heal() {
        this.health.set(this.maxHealth);
    }

    public boolean isDead() {
        return this.health.get() <= 0;
    }

    public List<Move> getMoves() {
        return List.copyOf(this.moves);
    }

    public void applyEffect(StatusEffect effect) {
        this.activeEffects.merge(effect, effect.getRounds(), (key, value) -> Math.min(value + 1, Constants.EFFECT_MAX_ROUNDS));
    }

    public boolean hasEffect(StatusEffect effect) {
        return this.activeEffects.containsKey(effect);
    }

    public Map<StatusEffect, Integer> getEffects() {
        return Map.copyOf(this.activeEffects);
    }

    public void cure() {
        Set<StatusEffect> negativeEffects = this.activeEffects.keySet()
                .stream()
                .filter(StatusEffect::isHarmful)
                .collect(Collectors.toSet());

        negativeEffects.forEach(this.activeEffects::remove);
    }

    @Override
    public void tick() {
        this.tickables.forEach(Tickable::tick);
        Set<StatusEffect> expired = new HashSet<>();
        for (Map.Entry<StatusEffect, Integer> entry : this.activeEffects.entrySet()) {
            int rounds = entry.getValue() - 1;

            if (rounds < 0) expired.add(entry.getKey());
            else entry.setValue(rounds);
        }

        expired.forEach(this.activeEffects::remove);
    }

    @Override
    public String toString() {
        return "Entity { id=%d, hostile=%b, health=%d }".formatted(this.id, this.hostile, this.health.get());
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
        private final AtomicInteger nextId;

        Factory(SignalDispatcher dispatcher) {
            this.dispatcher = dispatcher;
            this.nextId = new AtomicInteger(0);
        }

        Entity create(boolean hostile, int maxHeath, List<Move> moves) {
            Entity entity =  new Entity(this.nextId.getAndIncrement(), hostile, maxHeath, moves, this.dispatcher);
            LOGGER.info("Created entity %s".formatted(entity));
            return entity;
        }
    }
}
