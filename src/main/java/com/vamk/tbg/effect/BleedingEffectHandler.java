package com.vamk.tbg.effect;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import java.util.Random;
import java.util.logging.Logger;

public class BleedingEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(BleedingEffectHandler.class);
    private final Config config;

    public BleedingEffectHandler(Config config) {
        this.config = config;
    }

    @Override
    public void applyTo(Entity entity) {
        if (!entity.hasEffect(StatusEffect.BLEEDING)) return;

        int damage = RandomUtil.random(1, (int) (entity.getMaxHealth() * this.config.get(Keys.BLEEDING_MODIFIER)));
        entity.damage(damage);
        LOGGER.info("Entity %d loses %d health to bleeding".formatted(entity.getId(), damage));
    }
}
