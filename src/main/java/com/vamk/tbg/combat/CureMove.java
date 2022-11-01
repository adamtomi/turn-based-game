package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

public class CureMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(CureMove.class);

    public CureMove() {
        super("MOVE", false, true);
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();
        target.cure();
        LOGGER.info("Entity %d has been cured thanks to %d".formatted(target.getId(), source.getId()));
    }
}