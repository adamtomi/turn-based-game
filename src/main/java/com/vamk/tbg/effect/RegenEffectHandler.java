package com.vamk.tbg.effect;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class RegenEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(RegenEffectHandler.class);
    private final Random random;
    private final Config config;

    public RegenEffectHandler(Config config) {
        this.random = new Random();
        this.config = config;
    }

    @Override
    public void applyTo(Entity entity) {
        if (!entity.hasEffect(StatusEffect.REGENERATION)) return;

        int hp = this.random.nextInt(1, (int) (entity.getMaxHealth() * this.config.get(Keys.REGEN_KEY)));
        entity.heal(hp);
        LOGGER.info("Entity %d gains %d health thanks to regeneration".formatted(entity.getId(), hp));
    }
}
