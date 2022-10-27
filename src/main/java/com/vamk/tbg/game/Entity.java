package com.vamk.tbg.game;

import com.vamk.tbg.Constants;
import com.vamk.tbg.combat.FriendlyMove;
import com.vamk.tbg.combat.HostileMove;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.util.Tickable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Entity implements Tickable {
    private final Map<StatusEffect, Integer> activeEffects = new HashMap<>();
    private final Move friendlyMove = new FriendlyMove();
    private final Move hostileMove = new HostileMove();
    private final int id;
    private final boolean hostile;
    private final int maxHealth;
    private int health;

    public Entity(int id, boolean hostile, int maxHealth) {
        this.id = id;
        this.hostile = hostile;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public int getId() {
        return this.id;
    }

    public boolean isHostile() {
        return this.hostile;
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

    public boolean isDead() {
        return this.health <= 0;
    }

    public Move getFriendlyMove() {
        return this.friendlyMove;
    }

    public Move getHostileMove() {
        return this.hostileMove;
    }

    public void applyEffect(StatusEffect effect) {
        this.activeEffects.merge(effect, effect.getRounds(), (key, value) -> Math.min(value + 1, Constants.EFFECT_MAX_ROUNDS));
    }

    public boolean hasEffect(StatusEffect effect) {
        return this.activeEffects.containsKey(effect);
    }

    public void clearEffects() {
        this.activeEffects.clear();
    }

    public Set<StatusEffect> getEffects() {
        return Set.copyOf(this.activeEffects.keySet());
    }

    @Override
    public void tick() {
        Set<StatusEffect> expired = new HashSet<>();
        for (Map.Entry<StatusEffect, Integer> entry : this.activeEffects.entrySet()) {
            int rounds = entry.getValue() - 1;

            if (rounds <= 0) expired.add(entry.getKey());
            else entry.setValue(rounds);
        }

        expired.forEach(this.activeEffects::remove);
    }

    @Override
    public String toString() {
        return "Entity{ id=%d, hostile=%b, health=%d }".formatted(this.id, this.hostile, this.health);
    }
}
