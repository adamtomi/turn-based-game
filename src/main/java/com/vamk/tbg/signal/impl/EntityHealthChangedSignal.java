package com.vamk.tbg.signal.impl;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.Signal;

/**
 * This signal is dispatched every time an entity's
 * health changes (regardless of whether it increases
 * or decreases).
 */
public class EntityHealthChangedSignal implements Signal {
    private static final String ID = "ENTITY_HEALTH_CHANGED";
    private final Entity entity;
    private final int previous;

    public EntityHealthChangedSignal(Entity entity, int previous) {
        this.entity = entity;
        this.previous = previous;
    }

    @Override
    public String getId() {
        return ID;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public int getPrevious() {
        return this.previous;
    }

    @Override
    public String toString() {
        return "EntityHealthChangedSignal { entity=%s, previous=%d }".formatted(this.entity, this.previous);
    }
}
