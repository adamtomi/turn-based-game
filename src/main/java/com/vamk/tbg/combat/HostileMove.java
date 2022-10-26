package com.vamk.tbg.combat;

import com.vamk.tbg.Entity;
import com.vamk.tbg.effect.StatusEffect;

import java.util.Random;

public class HostileMove implements Move {
    private final Random random = new Random();

    @Override
    public void perform(Entity target) {
        int dmg = this.random.nextInt(target.getMaxHealth() / 2);
        target.damage(dmg);
        System.out.println("Dealing %d damage to entity: %s".formatted(dmg, target));

        int bleedingChance = random.nextInt(100);
        if (bleedingChance < 25) {
            System.out.println("Amazing, entity %s is now bleeding...".formatted(target));
            target.setEffect(StatusEffect.BLEEDING);
        }
    }
}
