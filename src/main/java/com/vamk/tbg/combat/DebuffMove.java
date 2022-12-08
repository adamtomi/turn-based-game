package com.vamk.tbg.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;

import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Logger;

import static com.vamk.tbg.util.RandomUtil.chance;

public class DebuffMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(DebuffMove.class);
    private static final EnumSet<StatusEffect> POTENTIAL_EFFECTS = EnumSet.of(
            StatusEffect.BLEEDING, StatusEffect.CONFUSED, StatusEffect.FROZEN
    );
    private final Config config;

    public DebuffMove(Config config) {
        super("DEBUFF", true);
        this.config = config;
    }

    @Override
    public void perform(MoveContext context) {
        Entity target = context.target();
        int dmg = (int) (target.getHealth() * 0.1);
        target.damage(dmg);
        LOGGER.info("Entity %d is lost %d hp thanks to %d".formatted(target.getId(), dmg, context.source().getId()));

        Map<String, Integer> chances = this.config.get(Keys.DEBUFF_CHANCES);
        POTENTIAL_EFFECTS.forEach(x -> testAndApply(chances, x, target));
    }

    private void testAndApply(Map<String, Integer> chances, StatusEffect effect, Entity target) {
        int chance = chances.get(effect.name());
        if (chance(chance)) { // Isn't this just fabulous?
            target.applyEffect(effect);
            LOGGER.info("Applying effect %s to entity".formatted(effect));
        }
    }
}
