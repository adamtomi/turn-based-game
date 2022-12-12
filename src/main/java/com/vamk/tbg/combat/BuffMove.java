package com.vamk.tbg.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.EnumSet;
import java.util.logging.Logger;

/**
 * Heals the targeted entity by a small amount
 * and potentially applies some non-harmful effects.
 */
public class BuffMove extends AbstractBuffMove {
    private static final Logger LOGGER = LogUtil.getLogger(BuffMove.class);
    private static final EnumSet<StatusEffect> POTENTIAL_EFFECTS = EnumSet.of(
            StatusEffect.REGENERATION, StatusEffect.LIFESTEAL, StatusEffect.CAFFEINATED
    );

    public BuffMove(Config config) {
        super("BUFF", false, config, Keys.BUFF_CHANCES, POTENTIAL_EFFECTS);
    }

    @Override
    protected void doPerform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();
        int hp = target.getHealth() / 10;
        target.heal(hp);
        LOGGER.info("Entity %d has gained %d HP back thanks to %d".formatted(target.getId(), hp, source.getId()));
    }
}
