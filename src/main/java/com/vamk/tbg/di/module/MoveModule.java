package com.vamk.tbg.di.module;

import com.vamk.tbg.di.qualifier.MoveSet;
import com.vamk.tbg.game.combat.BuffMove;
import com.vamk.tbg.game.combat.CureMove;
import com.vamk.tbg.game.combat.DamageMove;
import com.vamk.tbg.game.combat.DebuffMove;
import com.vamk.tbg.game.combat.HealAllMove;
import com.vamk.tbg.game.combat.HealMove;
import com.vamk.tbg.game.combat.Move;
import com.vamk.tbg.game.combat.SplashDamageMove;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface MoveModule {

    @Binds
    @IntoSet
    @MoveSet
    Move bindBuffMove(BuffMove move);

    @Binds
    @IntoSet
    @MoveSet
    Move bindCureMove(CureMove move);

    @Binds
    @IntoSet
    @MoveSet
    Move bindDamageMove(DamageMove move);

    @Binds
    @IntoSet
    @MoveSet
    Move bindDebuffMove(DebuffMove move);

    @Binds
    @IntoSet
    @MoveSet
    Move bindHealAllMove(HealAllMove move);

    @Binds
    @IntoSet
    @MoveSet
    Move bindHealMove(HealMove move);

    @Binds
    @IntoSet
    @MoveSet
    Move bindSplashDamageMove(SplashDamageMove move);
}
