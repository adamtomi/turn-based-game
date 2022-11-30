package com.vamk.tbg.signal.impl;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.Signal;

public class UserReadySignal implements Signal {
    private static final String ID = "USER_READY";
    private final Entity target;
    private final int moveIndex;

    public UserReadySignal(Entity target, int moveIndex) {
        this.target = target;
        this.moveIndex = moveIndex;
    }

    @Override
    public String getId() {
        return ID;
    }

    public Entity getTarget() {
        return this.target;
    }

    public int getMoveIndex() {
        return this.moveIndex;
    }

    @Override
    public String toString() {
        return "UserReadySignal { target=%s, moveIndex=%d }".formatted(this.target, this.moveIndex);
    }
}
