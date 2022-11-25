package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

public class HealAllMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(HealAllMove.class);

    public HealAllMove() {
        super("HEAL_ALL", false);
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        for (Entity entity : context.allEntities()) {
            // Only heal friendly entities
            if (source.isEnemyOf(entity)) continue;

            int hp = (int) (entity.getHealth() * 0.1);
            entity.heal(hp);
        }

        // The target of this move gets extra health
        Entity target = context.target();
        int hp = (int) (target.getMaxHealth() * 0.1);
        target.heal(hp);
        LOGGER.info("Entity %d healed all their teammates".formatted(context.source().getId()));
    }
}
