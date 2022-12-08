package com.vamk.tbg.command.mapper;

import com.vamk.tbg.effect.StatusEffect;

import java.util.Arrays;

public class StatusEffectMapper implements ArgumentMapper<StatusEffect> {

    @Override
    public Class<StatusEffect> type() {
        return StatusEffect.class;
    }

    @Override
    public StatusEffect map(String arg) throws ArgumentException {
        return Arrays.stream(StatusEffect.values())
                .filter(x -> x.name().equalsIgnoreCase(arg))
                .findFirst()
                .orElseThrow(() -> new ArgumentException(this, arg));
    }
}
