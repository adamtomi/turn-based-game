package com.vamk.tbg.game.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.game.effect.StatusEffect;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import javax.inject.Inject;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This is the damage equivalent of HealAllMove.
 * All hostile entities will lose some health,
 * the target will lose the most.
 */
public class SplashDamageMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(SplashDamageMove.class);
    private final Config config;

    @Inject
    public SplashDamageMove(Config config) {
        super("SPLASH_DAMAGE", true);
        this.config = config;
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Map<String, Integer> config = this.config.get(Keys.SPLASH_DAMAGE);
        int allModifier = config.get("all");
        int bleedingChance = config.get("bleeding-chance");

        for (Entity entity : context.allEntities()) {
            // Don't damage friendly entities
            if (!entity.isEnemyOf(source)) continue;

            if (RandomUtil.chance(bleedingChance)) {
                entity.applyEffect(StatusEffect.BLEEDING);
                LOGGER.info("Entity %d is now bleeding".formatted(entity.getId()));
            }

            int dmg = entity.getMaxHealth() / allModifier;
            entity.damage(dmg);
        }

        // Target loses extra health
        Entity target = context.target();
        int hp = target.getMaxHealth() / config.get("source");
        target.damage(hp);

        LOGGER.info("Entity %d damaged all its enemies...".formatted(source.getId()));
    }
}
