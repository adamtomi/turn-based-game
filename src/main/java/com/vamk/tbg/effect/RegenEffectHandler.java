package com.vamk.tbg.effect;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class RegenEffectHandler implements StatusEffectHandler {
    private static final Logger LOGGER = LogUtil.getLogger(RegenEffectHandler.class);
    private final Random random = new Random();
    private final Game game;

    public RegenEffectHandler(Game game) {
        this.game = game;
    }

    @Override
    public void tick() {
        for (Entity entity : this.game.getEntities()) {
            if (!entity.hasEffect(StatusEffect.REGENERATION)) continue;

            // TODO make this configurable
            int hp = this.random.nextInt(Math.max((int) (entity.getHealth() * 0.01), 1));
            entity.heal(hp);
            LOGGER.info("Entity %d gains %d health thanks to regeneration".formatted(entity.getId(), hp));
        }
    }
}
