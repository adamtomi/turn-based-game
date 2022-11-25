package com.vamk.tbg.effect;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class RegenEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(RegenEffectHandler.class);
    private final Random random;

    public RegenEffectHandler() {
        this.random = new Random();
    }

    @Override
    public void applyTo(Entity entity) {
        if (!entity.hasEffect(StatusEffect.REGENERATION)) return;

        // TODO make this configurable
        int hp = this.random.nextInt(Math.max((int) (entity.getMaxHealth() * 0.01), 1));
        entity.heal(hp);
        LOGGER.info("Entity %d gains %d health thanks to regeneration".formatted(entity.getId(), hp));
    }
}
