package com.vamk.tbg.combat;

public abstract class AbstractMove implements Move {
    private final String id;
    private final boolean attack;
    private final boolean targeted;

    AbstractMove(String id, boolean attack, boolean targeted) {
        this.id = id;
        this.attack = attack;
        this.targeted = targeted;
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
    public boolean isTargeted() {
        return this.targeted;
    }
}
