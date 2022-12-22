package com.vamk.tbg.di.module;

import com.vamk.tbg.di.qualifier.EffectHandlerSet;
import com.vamk.tbg.game.effect.BleedingEffectHandler;
import com.vamk.tbg.game.effect.RegenEffectHandler;
import com.vamk.tbg.game.effect.StatusEffectHandler;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface EffectHandlerModule {

    @Binds
    @IntoSet
    @EffectHandlerSet
    StatusEffectHandler bindBleedingHandler(BleedingEffectHandler handler);

    @Binds
    @IntoSet
    @EffectHandlerSet
    StatusEffectHandler bindRegenHandler(RegenEffectHandler handler);
}
