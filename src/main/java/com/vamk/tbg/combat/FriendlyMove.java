package com.vamk.tbg.combat;

import com.vamk.tbg.Entity;
import com.vamk.tbg.effect.StatusEffect;

public class FriendlyMove implements Move {

    @Override
    public void perform(Entity target) {
        int effect = ((int) (Math.random() * 100)) % 2 == 0 ? StatusEffect.REGENERATION : StatusEffect.LIFESTEAL;
        target.setEffect(effect);
        System.out.println("Applying effect %d to entity: %s".formatted(effect, target));
    }
}
