package com.vamk.tbg.game;

import com.vamk.tbg.Constants;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.BleedingEffectHandler;
import com.vamk.tbg.effect.RegenEffectHandler;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.util.Tickable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Entity implements Tickable {
    private final Map<StatusEffect, Integer> activeEffects = new HashMap<>();
    private final Set<Tickable> tickables = Set.of(
            new BleedingEffectHandler(this),
            new RegenEffectHandler(this)
    );
    private final int id;
    private final boolean hostile;
    private final int maxHealth;
    private int health;
    private final List<Move> moves;

    public Entity(int id, boolean hostile, int maxHealth, List<Move> moves) {
        this.id = id;
        this.hostile = hostile;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.moves = moves;
    }

    public int getId() {
        return this.id;
    }

    public boolean isHostile() {
        return this.hostile;
    }

    public boolean isEnemyOf(Entity that) {
        return that.hostile != this.hostile;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public int getHealth() {
        return this.health;
    }

    public void damage(int dmg) {
        this.health = Math.max(0, this.health - dmg);
    }

    public void heal(int hp) {
        this.health = Math.min(this.health + hp, this.maxHealth);
    }

    public void heal() {
        this.health = this.maxHealth;
    }

    public boolean isDead() {
        return this.health <= 0;
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

    public Set<StatusEffect> getEffects() {
        return Set.copyOf(this.activeEffects.keySet());
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
        return "Entity{ id=%d, hostile=%b, health=%d }".formatted(this.id, this.hostile, this.health);
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
}
