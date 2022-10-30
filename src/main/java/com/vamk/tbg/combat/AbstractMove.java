package com.vamk.tbg.combat;

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
}
