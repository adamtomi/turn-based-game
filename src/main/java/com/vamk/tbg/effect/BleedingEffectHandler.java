package com.vamk.tbg.effect;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class BleedingEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(BleedingEffectHandler.class);
    private final Random random;

    public BleedingEffectHandler() {
        this.random = new Random();
    }

    @Override
    public void applyTo(Entity entity) {
        if (!entity.hasEffect(StatusEffect.BLEEDING)) return;

        // TODO make this configurable
        int damage = this.random.nextInt(Math.max((int) (entity.getMaxHealth() * 0.05), 1));
        entity.damage(damage);
        LOGGER.info("Entity %d loses %d health to bleeding".formatted(entity.getId(), damage));
    }
}
