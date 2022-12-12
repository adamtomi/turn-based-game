package com.vamk.tbg.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import java.util.Map;
import java.util.logging.Logger;

/**
 * A generic damage move, targets one entity,
 * deals great damage and with a certain chance,
 * it applies bleeding to the target.
 */
public class DamageMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(DamageMove.class);
    private final Config config;

    public DamageMove(Config config) {
        super("DAMAGE", true);
        this.config = config;
    }

    @Override
    public void perform(MoveContext context) {
        Entity target = context.target();
        Entity source = context.source();
        Map<String, Integer> config = this.config.get(Keys.DAMAGE_CONFIG);

        int dmg = RandomUtil.random(target.getMaxHealth() / config.get("min"), target.getMaxHealth() / config.get("max"));
        target.damage(dmg);
        LOGGER.info("Dealing %d damage to entity: %d".formatted(dmg, target.getId()));
        if (source.hasEffect(StatusEffect.LIFESTEAL)) {
            int hp = dmg / config.get("lifesteal-modifier");
            source.heal(hp);
            LOGGER.warning("Entity %d gains %d HP due to lifesteal".formatted(source.getId(), hp));
        }

        if (RandomUtil.chance(config.get("bleeding-chance"))) {
            LOGGER.info("Amazing, entity %d is now bleeding...".formatted(target.getId()));
            target.applyEffect(StatusEffect.BLEEDING);
        }
    }
}
