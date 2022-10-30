package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

public class DebuffMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(DebuffMove.class);
    private static final int

    public DebuffMove() {
        super("DEBUFF", true);
    }

    @Override
    public void perform(MoveContext context) {
        Entity target = context.target();
        target.applyEffect(StatusEffect.CONFUSED);
        int dmg = (int) (target.getHealth() * 0.1);

        target.damage(dmg);
        LOGGER.info("Entity %d is now confused and lost %d hp thanks to %d".formatted(target.getId(), dmg, context.source().getId()));
    }
}
