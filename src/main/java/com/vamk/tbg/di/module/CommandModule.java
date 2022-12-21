package com.vamk.tbg.di.module;

import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.impl.DamageCommand;
import com.vamk.tbg.command.impl.HealCommand;
import com.vamk.tbg.command.impl.KillCommand;
import com.vamk.tbg.command.impl.ListCommand;
import com.vamk.tbg.command.impl.ListEffectsCommand;
import com.vamk.tbg.command.impl.ListMovesCommand;
import com.vamk.tbg.command.impl.RemoveEffectCommand;
import com.vamk.tbg.command.impl.SetEffectCommand;
import com.vamk.tbg.di.qualifier.Commands;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface CommandModule {

    @Binds
    @IntoSet
    @Commands
    Command bindDamageCommand(DamageCommand command);

    @Binds
    @IntoSet
    @Commands
    Command bindHealCommand(HealCommand command);

    @Binds
    @IntoSet
    @Commands
    Command bindKillCommand(KillCommand command);

    @Binds
    @IntoSet
    @Commands
    Command bindListCommand(ListCommand command);

    @Binds
    @IntoSet
    @Commands
    Command bindListEffectCommand(ListEffectsCommand command);

    @Binds
    @IntoSet
    @Commands
    Command bindListMovesCommand(ListMovesCommand command);

    @Binds
    @IntoSet
    @Commands
    Command bindRemoveEffectCommand(RemoveEffectCommand command);

    @Binds
    @IntoSet
    @Commands
    Command bindSetEffectCommand(SetEffectCommand command);
}
