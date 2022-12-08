package com.vamk.tbg.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.vamk.tbg.util.RandomUtil.chance;

// TODO Somehow merge buff and debuff moves, as they're almost the same
public class BuffMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(BuffMove.class);
    private final Config config;

    public BuffMove(Config config) {
        super("BUFF", false);
        this.config = config;
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();

        int hp = target.getHealth() / 10;
        target.heal(hp);
        LOGGER.info("Entity %d has gained %d HP back thanks to %d".formatted(target.getId(), hp, source.getId()));

        Map<String, Integer> chances = this.config.get(Keys.BUFF_CHANCES);
        if (chance(chances.get(StatusEffect.REGENERATION.name()))) {
            target.applyEffect(StatusEffect.REGENERATION);
            LOGGER.info("Entity also gained regeneration");
        }

        if (chance(chances.get(StatusEffect.LIFESTEAL.name()))) {
            target.applyEffect(StatusEffect.LIFESTEAL);
            LOGGER.info("Entity also has lifesteal now");
        }

        if (chance(chances.get(StatusEffect.CAFFEINATED.name()))) {
            target.applyEffect(StatusEffect.CAFFEINATED);
            LOGGER.info("Entity is also caffeinated now");
        }
    }
}
