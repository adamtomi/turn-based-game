package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

import static com.vamk.tbg.util.RandomUtil.chance;

public class HealMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(HealMove.class);
    private static final int REGENERATION_CHANCE = 15;
    private static final int LIFESTEAL_CHANCE = 10;

    public HealMove() {
        super("HEAL", false);
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();
        target.heal();
        LOGGER.info("Entity %d has been healed by %d".formatted(target.getId(), source.getId()));

        if (chance(REGENERATION_CHANCE)) {
            target.applyEffect(StatusEffect.REGENERATION);
            LOGGER.info("They also got regeneration now");
        }

        if (chance(LIFESTEAL_CHANCE)) {
            target.applyEffect(StatusEffect.LIFESTEAL);
            LOGGER.info("They also got lifesteal now");
        }
    }
}
