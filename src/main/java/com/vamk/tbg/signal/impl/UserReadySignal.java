package com.vamk.tbg.signal.impl;

import com.vamk.tbg.combat.Move;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.Signal;

/**
 * Dispatched once the user has selected a move
 * and a target entity as well.
 */
public class UserReadySignal implements Signal {
    private static final String ID = "USER_READY";
    private final Entity target;
    private final Move move;

    public UserReadySignal(Entity target, Move move) {
        this.target = target;
        this.move = move;
    }

    @Override
    public String getId() {
        return ID;
    }

    public Entity getTarget() {
        return this.target;
    }

    public Move getMove() {
        return this.move;
    }

    @Override
    public String toString() {
        return "UserReadySignal { target=%s, move=%s }".formatted(this.target, this.move.getId());
    }
}
