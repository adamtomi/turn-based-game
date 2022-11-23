package com.vamk.tbg.signal.impl;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.Signal;

public class EntityDeathSignal implements Signal {
    private static final String ID = "ENTITY_DEATH";
    private final Entity entity;

    public EntityDeathSignal(Entity entity) {
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
        return "EntityDeathSignal { entity=%s }".formatted(this.entity);
    }
}
