package com.vamk.tbg.di.module;

import com.vamk.tbg.command.mapper.ArgumentMapper;
import com.vamk.tbg.command.mapper.EntityMapper;
import com.vamk.tbg.command.mapper.IntMapper;
import com.vamk.tbg.command.mapper.StatusEffectMapper;
import com.vamk.tbg.di.qualifier.MapperSet;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface ArgumentMapperModule {

    @Binds
    @IntoSet
    @MapperSet
    ArgumentMapper<?> bindEntityMapper(EntityMapper mapper);

    @Binds
    @IntoSet
    @MapperSet
    ArgumentMapper<?> bindIntMapper(IntMapper mapper);

    @Binds
    @IntoSet
    @MapperSet
    ArgumentMapper<?> bindStatusEffectMapper(StatusEffectMapper mapper);
}
