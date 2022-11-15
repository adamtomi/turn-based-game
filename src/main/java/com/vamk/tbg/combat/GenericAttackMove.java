package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class GenericAttackMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(GenericAttackMove.class);
    private final Random random;

    public GenericAttackMove() {
        super("GENERIC_ATTACK", true, true);
        this.random = new Random();
    }

    @Override
    public void perform(MoveContext context) {
        Entity target = context.target();
        Entity source = context.source();
        int dmg = this.random.nextInt(target.getMaxHealth() / 10, target.getMaxHealth() / 5);
        target.damage(dmg);
        LOGGER.info("Dealing %d damage to entity: %d".formatted(dmg, target.getId()));
        if (source.hasEffect(StatusEffect.LIFESTEAL)) {
            int hp = dmg / 5;
            source.heal(hp);
            LOGGER.warning("Entity %d gains %d HP due to lifesteal".formatted(source.getId(), hp));
        }

        int bleedingChance = this.random.nextInt(100);
        if (bleedingChance < 10) {
            LOGGER.info("Amazing, entity %d is now bleeding...".formatted(target.getId()));
            target.applyEffect(StatusEffect.BLEEDING);
        }
    }
}
