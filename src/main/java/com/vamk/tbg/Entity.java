package com.vamk.tbg;

import com.vamk.tbg.combat.FriendlyMove;
import com.vamk.tbg.combat.HostileMove;
import com.vamk.tbg.combat.Move;
import com.vamk.tbg.effect.StatusEffectHolder;

public class Entity extends StatusEffectHolder {
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

    @Override
    public String toString() {
        return "Entity{ id=%d, hostile=%b, health=%d }".formatted(this.id, this.hostile, this.health);
    }
}
