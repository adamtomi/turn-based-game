package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

import static com.vamk.tbg.util.RandomUtil.chance;

public class DebuffMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(DebuffMove.class);
    private static final int BLEEDING_CHANCE = 50;
    private static final int FROZEN_CHANCE = 10;
    private static final int CONFUSED_CHANCE = 5;

    public DebuffMove() {
        super("DEBUFF", true);
    }

    @Override
    public void perform(MoveContext context) {
        Entity target = context.target();
        int dmg = (int) (target.getHealth().get() * 0.1);
        target.damage(dmg);
        LOGGER.info("Entity %d is lost %d hp thanks to %d".formatted(target.getId(), dmg, context.source().getId()));

        if (chance(BLEEDING_CHANCE)) {
            target.applyEffect(StatusEffect.BLEEDING);
            LOGGER.info("Entity %d is also bleeding now".formatted(target.getId()));
        }

         if (chance(FROZEN_CHANCE)) {
             target.applyEffect(StatusEffect.FROZEN);
             LOGGER.info("Entity %d is also frozen now".formatted(target.getId()));
         }

         if (chance(CONFUSED_CHANCE)) {
             target.applyEffect(StatusEffect.CONFUSED);
             LOGGER.info("Entity %d is also confused now".formatted(target.getId()));
         }
    }
}
