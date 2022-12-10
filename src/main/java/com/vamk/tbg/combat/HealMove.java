package com.vamk.tbg.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import java.util.Map;
import java.util.logging.Logger;

public class HealMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(HealMove.class);
    private final Config config;

    public HealMove(Config config) {
        super("HEAL", false);
        this.config = config;
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();
        Map<String, Integer> config = this.config.get(Keys.HEAL);

        int hp = RandomUtil.random(target.getMaxHealth() / config.get("min"), target.getMaxHealth() / config.get("max"));
        target.heal(hp);

        LOGGER.info("Entity %d has been healed by %d".formatted(target.getId(), source.getId()));
    }

    @Override
    public boolean isApplicableTo(Entity entity) {
        return entity.getHealth() < entity.getMaxHealth();
    }
}
