package com.vamk.tbg.effect;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class BleedingEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(BleedingEffectHandler.class);
    private final Random random = new Random();
    private final Game game;

    public BleedingEffectHandler(Game game) {
        this.game = game;
    }

    @Override
    public void tick() {
        for (Entity entity : this.game.getEntities()) {
            if (!entity.hasEffect(StatusEffect.BLEEDING)) continue;

            // TODO make this configurable
            int damage = this.random.nextInt(Math.max((int) (entity.getHealth() * 0.05), 1));
            entity.damage(damage);
            LOGGER.info("Entity %d loses %d health to bleeding".formatted(entity.getId(), damage));
        }
    }
}
