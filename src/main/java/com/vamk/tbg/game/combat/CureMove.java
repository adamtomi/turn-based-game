package com.vamk.tbg.game.combat;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.game.effect.StatusEffect;
import com.vamk.tbg.util.LogUtil;

import javax.inject.Inject;
import java.util.logging.Logger;

public class CureMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(CureMove.class);

    @Inject
    public CureMove() {
        super("CURE", false);
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();
        target.cure();
        LOGGER.info("Entity %d has been cured thanks to %d".formatted(target.getId(), source.getId()));
    }

    /**
     * This move cannot be used on entities that don't
     * have any harmful effects on them.
     */
    @Override
    public boolean isApplicableTo(Entity entity) {
        int harmfulEffects = (int) entity.getEffects().keySet()
                .stream()
                .filter(StatusEffect::isHarmful)
                .count();
        return harmfulEffects > 0;
    }
}
