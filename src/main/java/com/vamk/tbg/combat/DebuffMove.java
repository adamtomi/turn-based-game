package com.vamk.tbg.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.EnumSet;
import java.util.logging.Logger;

public class DebuffMove extends AbstractBuffMove {
    private static final Logger LOGGER = LogUtil.getLogger(DebuffMove.class);
    private static final EnumSet<StatusEffect> POTENTIAL_EFFECTS = EnumSet.of(
            StatusEffect.BLEEDING, StatusEffect.CONFUSED, StatusEffect.FROZEN
    );

    public DebuffMove(Config config) {
        super("DEBUFF", true, config, Keys.DEBUFF_CHANCES, POTENTIAL_EFFECTS);
    }

    @Override
    public void doPerform(MoveContext context) {
        Entity target = context.target();
        int dmg = (int) (target.getHealth() * 0.1);
        target.damage(dmg);
        LOGGER.info("Entity %d is lost %d hp thanks to %d".formatted(target.getId(), dmg, context.source().getId()));

    }
}
