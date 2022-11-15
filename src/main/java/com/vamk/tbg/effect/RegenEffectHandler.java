package com.vamk.tbg.effect;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class RegenEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(RegenEffectHandler.class);
    private final Random random;
    private final Entity entity;

    public RegenEffectHandler(Entity entity) {
        this.random = new Random();
        this.entity = entity;
    }

    @Override
    public void tick() {
        if (!this.entity.hasEffect(StatusEffect.REGENERATION)) return;

        // TODO make this configurable
        int hp = this.random.nextInt(Math.max((int) (this.entity.getHealth() * 0.01), 1));
        this.entity.heal(hp);
        LOGGER.info("Entity %d gains %d health thanks to regeneration".formatted(this.entity.getId(), hp));
    }
}
