package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

import static com.vamk.tbg.util.RandomUtil.chance;

public class BuffMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(BuffMove.class);
    private static final int REGEN_CHANCE = 40;
    private static final int LIFESTEAL_CHANCE = 10;
    private static final int CAFFEINATED_CHANCE = 5;

    public BuffMove() {
        super("BUFF", false);
    }

    @Override
    public void perform(MoveContext context) {
        Entity source = context.source();
        Entity target = context.target();

        int hp = target.getHealth() / 10;
        target.heal(hp);
        LOGGER.info("Entity %d has gained %d HP back thanks to %d".formatted(target.getId(), hp, source.getId()));
        if (chance(REGEN_CHANCE)) {
            target.applyEffect(StatusEffect.REGENERATION);
            LOGGER.info("Entity also gained regeneration");
        }

        if (chance(LIFESTEAL_CHANCE)) {
            target.applyEffect(StatusEffect.LIFESTEAL);
            LOGGER.info("Entity also has lifesteal now");
        }

        if (chance(CAFFEINATED_CHANCE)) {
            target.applyEffect(StatusEffect.CAFFEINATED);
            LOGGER.info("Entity is also caffeinated now");
        }
    }
}
