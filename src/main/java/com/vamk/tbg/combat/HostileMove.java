package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;

import java.util.Random;
import java.util.logging.Logger;

public class HostileMove implements Move {
    private static final Logger LOGGER = LogUtil.getLogger(HostileMove.class);
    private final Random random = new Random();

    @Override
    public boolean isAttack() {
        return true;
    }

    @Override
    public void perform(Entity source, Entity target) {
        int dmg = this.random.nextInt(target.getMaxHealth() / 5);
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
