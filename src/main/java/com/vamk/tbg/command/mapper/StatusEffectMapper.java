package com.vamk.tbg.command.mapper;

import com.vamk.tbg.game.effect.StatusEffect;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * A mapper that turns strings into {@link StatusEffect} objects.
 */
public class StatusEffectMapper implements ArgumentMapper<StatusEffect> {

    @Inject
    public StatusEffectMapper() {}

    @Override
    public Class<StatusEffect> type() {
        return StatusEffect.class;
    }

    /**
     * If there's no {@link StatusEffect} with this name, throw
     * an exception. This operation is case-insensitive.
     */
    @Override
    public StatusEffect map(String arg) throws ArgumentException {
        return Arrays.stream(StatusEffect.values())
                .filter(x -> x.name().equalsIgnoreCase(arg))
                .findFirst()
                .orElseThrow(() -> new ArgumentException(this, arg));
    }
}
