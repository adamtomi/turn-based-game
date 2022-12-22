package com.vamk.tbg.di;

import com.vamk.tbg.Bootstrap;
import com.vamk.tbg.di.module.ArgumentMapperModule;
import com.vamk.tbg.di.module.CommandModule;
import com.vamk.tbg.di.module.EffectHandlerModule;
import com.vamk.tbg.di.module.MoveModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        ArgumentMapperModule.class,
        CommandModule.class,
        EffectHandlerModule.class,
        MoveModule.class
})
public interface GameComponent {

    Bootstrap bootstrap();
}
