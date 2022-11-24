package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;

import java.io.Serial;

public abstract class AbstractMove implements Move {
    @Serial
    private static final long serialVersionUID = 1008816914619703220L;
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
