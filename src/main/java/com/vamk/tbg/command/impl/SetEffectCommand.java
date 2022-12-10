package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Argument;
import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;

public class SetEffectCommand extends Command {

    public SetEffectCommand() {
        super(
                "set-eff",
                "Apply the specified effect to the specified entity",
                new Argument("target", "Specify which entity to apply the effect to"),
                new Argument("effect", "Specify which effect to apply to the entity")
        );
    }

    @Override
    public void run(CommandContext context) throws CommandException {
        Entity target = context.nextArg(Entity.class);
        StatusEffect effect = context.nextArg(StatusEffect.class);
        target.applyEffect(effect);
        context.respond("Effect %s was applied to entity %d".formatted(effect.name(), target.getId()));
    }
}
