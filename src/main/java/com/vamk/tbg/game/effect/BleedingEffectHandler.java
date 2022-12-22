package com.vamk.tbg.game.effect;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Every turn the entity loses a chunk of its health.
 */
public class BleedingEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(BleedingEffectHandler.class);
    private final Config config;

    @Inject
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
