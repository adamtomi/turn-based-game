package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Argument;
import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.game.Entity;

public class HealCommand extends Command {

    public HealCommand() {
        super(
                "heal",
                "Heals the specified entity by the specified amount",
                new Argument("target", "Specify the ID of the entity to be healed"),
                new Argument("amount", "Specify by how much should the entity be healed")
        );
    }

    @Override
    public void run(CommandContext context) throws CommandException {
        Entity target = context.nextArg(Entity.class);
        if (context.remaining() > 0) {
            int amount = context.nextArg(Integer.TYPE);
            target.heal(amount);
        } else {
            target.heal();
        }
    }
}
