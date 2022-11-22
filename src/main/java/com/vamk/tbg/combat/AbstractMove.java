package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;

public abstract class AbstractMove implements Move {
    private final String id;
    private final boolean attack;

    AbstractMove(String id, boolean attack) {
        this.id = id;
        this.attack = attack;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isAttack() {
        return this.attack;
    }

    @Override
    public boolean isApplicableTo(Entity entity) {
        return true;
    }
}
