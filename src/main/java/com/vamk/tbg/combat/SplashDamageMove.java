package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

import static com.vamk.tbg.util.RandomUtil.chance;

public class SplashDamageMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(SplashDamageMove.class);
    private static final int BLEEDING_CHANCE = 50;

    public SplashDamageMove() {
        super("SPLASH_DAMAGE", true, false);
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        for (Entity entity : context.allEntities()) {
            // Don't damage friendly entities
            if (!entity.isEnemyOf(source)) return;

            if (chance(BLEEDING_CHANCE)) {
                entity.applyEffect(StatusEffect.BLEEDING);
                LOGGER.info("Entity %d is now bleeding".formatted(entity.getId()));
            }

            int dmg = entity.getMaxHealth() / 15;
            entity.damage(dmg);
        }

        LOGGER.info("Entity %d damaged all its enemies...".formatted(source.getId()));
    }
}
