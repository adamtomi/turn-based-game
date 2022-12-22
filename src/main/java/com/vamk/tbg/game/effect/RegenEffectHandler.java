package com.vamk.tbg.game.effect;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Every turn the entity gains some health back.
 */
public class RegenEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(RegenEffectHandler.class);
    private final Config config;

    @Inject
    public RegenEffectHandler(Config config) {
        this.config = config;
    }

    @Override
    public void applyTo(Entity entity) {
        if (!entity.hasEffect(StatusEffect.REGENERATION)) return;

        int hp = RandomUtil.random(1, (int) (entity.getMaxHealth() * this.config.get(Keys.REGEN_MODIFIER)));
        entity.heal(hp);
        LOGGER.info("Entity %d gains %d health thanks to regeneration".formatted(entity.getId(), hp));
    }
}
