package com.vamk.tbg.signal.impl;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.Signal;

/**
 * This signal is dispatched when an entity's effects
 * change (regardless of the cause).
 */
public class EffectsUpdatedSignal implements Signal {
    private static final String ID = "EFFECTS_UPDATED";
    private final Entity entity;

    public EffectsUpdatedSignal(Entity entity) {
        this.entity = entity;
    }

    @Override
    public String getId() {
        return ID;
    }

    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public String toString() {
        return "EffectsUpdatedSignal { entity=%s }".formatted(this.entity);
    }
}
