package com.vamk.tbg.signal.impl;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.Signal;

import java.util.List;

/**
 * Signal used to indicate that the game is ready,
 * meaning all the entities have been created and
 * the order in which they will play has been set.
 */
public class GameReadySignal implements Signal {
    private static final String ID = "GAME_READY";
    private final List<Entity> entities;

    public GameReadySignal(List<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public String getId() {
        return ID;
    }

    /**
     * List of entites that have been created.
     *
     * @return java.util.List
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    @Override
    public String toString() {
        return "GameReadySignal { entitis=%s }".formatted(this.entities);
    }
}
