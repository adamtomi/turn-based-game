package com.vamk.tbg.combat;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CombatRegistry {
    private final Map<String, Move> moves;

    public CombatRegistry() {
        this.moves = Stream.of(new BuffMove(), new CureMove(), new DebuffMove(), new GenericAttackMove(), new HealAllMove(), new HealMove(), new SplashDamageMove())
                .collect(Collectors.toMap(Move::getId, Function.identity()));
    }

    public Move findMove(String id) {
        return this.moves.get(id);
    }
}
