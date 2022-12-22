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
import com.vamk.tbg.di.qualifier.CommandSet;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public interface CommandModule {

    @Binds
    @IntoSet
    @CommandSet
    Command bindDamageCommand(DamageCommand command);

    @Binds
    @IntoSet
    @CommandSet
    Command bindHealCommand(HealCommand command);

    @Binds
    @IntoSet
    @CommandSet
    Command bindKillCommand(KillCommand command);

    @Binds
    @IntoSet
    Command bindListCommand(ListCommand command);

    @Binds
    @IntoSet
    Command bindListEffectCommand(ListEffectsCommand command);

    @Binds
    @IntoSet
    @CommandSet
    Command bindListMovesCommand(ListMovesCommand command);

    @Binds
    @IntoSet
    @CommandSet
    Command bindRemoveEffectCommand(RemoveEffectCommand command);

    @Binds
    @IntoSet
    @CommandSet
    Command bindSetEffectCommand(SetEffectCommand command);
}
