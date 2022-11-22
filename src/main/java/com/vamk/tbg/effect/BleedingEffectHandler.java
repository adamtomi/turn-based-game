package com.vamk.tbg.effect;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class BleedingEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(BleedingEffectHandler.class);
    private final Random random;
    private final Entity entity;

    public BleedingEffectHandler(Entity entity) {
        this.random = new Random();
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (!this.entity.hasEffect(StatusEffect.BLEEDING)) return;

        // TODO make this configurable
        int damage = this.random.nextInt(Math.max((int) (this.entity.getMaxHealth() * 0.05), 1));
        this.entity.damage(damage);
        LOGGER.info("Entity %d loses %d health to bleeding".formatted(this.entity.getId(), damage));
    }
}
