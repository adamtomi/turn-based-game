package com.vamk.tbg.game.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.game.effect.StatusEffect;
import com.vamk.tbg.util.LogUtil;

import javax.inject.Inject;
import java.util.EnumSet;
import java.util.logging.Logger;

/**
 * The target of this move will lose a small
 * amount of health and will likely get at least
 * (but probably) more negative status effects.
 */
public class DebuffMove extends AbstractBuffMove {
    private static final Logger LOGGER = LogUtil.getLogger(DebuffMove.class);
    private static final EnumSet<StatusEffect> POTENTIAL_EFFECTS = EnumSet.of(
            StatusEffect.BLEEDING, StatusEffect.CONFUSED, StatusEffect.FROZEN
    );

    @Inject
    public DebuffMove(Config config) {
        super("DEBUFF", true, config, Keys.DEBUFF_CHANCES, POTENTIAL_EFFECTS);
    }

    @Override
    protected void doPerform(MoveContext context) {
        Entity target = context.target();
        int dmg = (int) (target.getHealth() * 0.1);
        target.damage(dmg);
        LOGGER.info("Entity %d has lost %d hp thanks to %d".formatted(target.getId(), dmg, context.source().getId()));
    }
}
