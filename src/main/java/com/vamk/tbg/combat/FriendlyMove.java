package com.vamk.tbg.combat;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

public class FriendlyMove implements Move {
    private static final Logger LOGGER = LogUtil.getLogger(FriendlyMove.class);

    @Override
    public void perform(Entity source, Entity target) {
        StatusEffect effect = ((int) (Math.random() * 100)) % 2 == 0 ? StatusEffect.REGENERATION : StatusEffect.LIFESTEAL;
        source.applyEffect(effect);
        LOGGER.info("Applying effect %s to entity: %d".formatted(effect, source.getId()));
    }
}
