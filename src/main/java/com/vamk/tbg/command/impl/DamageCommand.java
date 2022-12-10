package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Argument;
import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.game.Entity;

public class DamageCommand extends Command {

    public DamageCommand() {
        super(
                "dmg",
                "Damage the specified entity by the specified amount",
                new Argument("target", "Specify the entity to damage"),
                new Argument("amount", "Specify by how much the entity should be damaged")
        );
    }

    @Override
    public void run(CommandContext context) throws CommandException {
        Entity target = context.nextArg(Entity.class);
        int amount = context.nextArg(Integer.class);
        target.damage(amount);
        context.respond("Entity %d was damaged by %d, its new health is %d".formatted(target.getId(), amount, target.getHealth()));
    }
}
