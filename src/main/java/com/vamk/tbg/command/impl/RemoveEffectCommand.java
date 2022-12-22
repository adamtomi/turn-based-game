package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Argument;
import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.effect.StatusEffect;

import javax.inject.Inject;

public class RemoveEffectCommand extends Command {

    @Inject
    public RemoveEffectCommand() {
        super(
                "rem-eff",
                "Removes the specified effect from the specified entity",
                new Argument("target", "Specify the ID of the entity from which the effect should be removed"),
                new Argument("effect", "Specify the effect that should be removed")
        );
    }

    @Override
    public void run(CommandContext context) throws CommandException {
        Entity target = context.nextArg(Entity.class);
        StatusEffect effect = context.nextArg(StatusEffect.class);
        target.removeEffect(effect);
        context.respond("Effect %s was removed from entity %d".formatted(effect.name(), target.getId()));
    }
}
