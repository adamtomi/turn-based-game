package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.io.Serial;
import java.util.logging.Logger;

public class CureMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(CureMove.class);
    @Serial
    private static final long serialVersionUID = 4985779855365158332L;

    public CureMove() {
        super("MOVE", false);
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();
        target.cure();
        LOGGER.info("Entity %d has been cured thanks to %d".formatted(target.getId(), source.getId()));
    }

    @Override
    public boolean isApplicableTo(Entity entity) {
        int harmfulEffects = (int) entity.getEffects().keySet()
                .stream()
                .filter(StatusEffect::isHarmful)
                .count();
        return harmfulEffects > 0;
    }
}
