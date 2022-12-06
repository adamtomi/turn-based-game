package com.vamk.tbg.command.mapper;

import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.effect.StatusEffect;

import java.util.Arrays;

public class StatusEffectMapper implements ArgumentMapper<StatusEffect> {
    public static final StatusEffectMapper INSTANCE = new StatusEffectMapper();

    private StatusEffectMapper() {}

    @Override
    public StatusEffect map(String arg) throws CommandException {
        return Arrays.stream(StatusEffect.values())
                .filter(x -> x.name().equalsIgnoreCase(arg))
                .findFirst()
                .orElseThrow(() -> new CommandException(""));
    }
}
