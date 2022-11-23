package com.vamk.tbg.signal.impl;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.Signal;

public class EntityPlaysSignal implements Signal {
    private static final String ID = "ENTITY_PLAYS_SIGNAL";
    private final Entity entity;
    private final boolean userControlled;

    public EntityPlaysSignal(Entity entity, boolean userControlled) {
        this.entity = entity;
        this.userControlled = userControlled;
    }

    @Override
    public String getId() {
        return ID;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public boolean isUserControlled() {
        return this.userControlled;
    }

    @Override
    public String toString() {
        return "EntityPlaysSignal { entity=%s, userControlled=%b }".formatted(this.entity, this.userControlled);
    }
}
