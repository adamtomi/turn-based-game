package com.vamk.tbg.combat;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.ConfigKey;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.RandomUtil;

import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Logger;

import static com.vamk.tbg.util.RandomUtil.chance;

public abstract class AbstractBuffMove extends AbstractMove {
    private static final Logger LOGGER = LogUtil.getLogger(AbstractBuffMove.class);
    private final Config config;
    private final ConfigKey<Map<String, Integer>> chancesKey;
    private final EnumSet<StatusEffect> potentialEffects;

    public AbstractBuffMove(String id, boolean attack, Config config, ConfigKey<Map<String, Integer>> chancesKey, EnumSet<StatusEffect> potentialEffects) {
        super(id, attack);
        this.config = config;
        this.chancesKey = chancesKey;
        this.potentialEffects = potentialEffects;
    }

    @Override
    public final void perform(MoveContext context) {
        doPerform(context);
        Entity target = context.target();
        Map<String, Integer> chances = this.config.get(this.chancesKey);
        this.potentialEffects.forEach(x -> testAndApply(chances, x, target));
    }

    protected abstract void doPerform(MoveContext context);

    private void testAndApply(Map<String, Integer> chances, StatusEffect effect, Entity target) {
        int chance = chances.get(effect.name());
        if (RandomUtil.chance(chance)) {
            target.applyEffect(effect);
            LOGGER.info("Applying effect %s to entity".formatted(effect));
        }
    }
}
