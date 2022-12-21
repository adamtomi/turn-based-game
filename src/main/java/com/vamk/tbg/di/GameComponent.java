package com.vamk.tbg.di;

import com.vamk.tbg.Bootstrap;
import com.vamk.tbg.di.module.CommandModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        CommandModule.class
})
public interface GameComponent {

    Bootstrap bootstrap();
}
