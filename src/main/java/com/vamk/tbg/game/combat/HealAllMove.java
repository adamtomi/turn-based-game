package com.vamk.tbg.game.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import javax.inject.Inject;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This move affects all friendly entities, however
 * it still needs a target. All entities will regain
 * some health with the target getting the most back.
 */
public class HealAllMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(HealAllMove.class);
    private final Config config;

    @Inject
    public HealAllMove(Config config) {
        super("HEAL_ALL", false);
        this.config = config;
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Map<String, Integer> config = this.config.get(Keys.HEAL_ALL);
        int allModifier = config.get("all");

        for (Entity entity : context.allEntities()) {
            // Only heal friendly entities
            if (source.isEnemyOf(entity)) continue;

            int hp = entity.getMaxHealth() / allModifier;
            entity.heal(hp);
        }

        // The target of this move gets extra health
        Entity target = context.target();
        int hp = target.getMaxHealth() / config.get("source");
        target.heal(hp);
        LOGGER.info("Entity %d healed all their teammates".formatted(context.source().getId()));
    }
}
