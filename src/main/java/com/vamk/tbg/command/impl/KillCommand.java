package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Argument;
import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.game.Entity;

public class KillCommand extends Command {

    public KillCommand() {
        super(
                "kill",
                "Kills the specified entity",
                new Argument("target", "Specify the ID of the entity to be killed")
        );
    }

    @Override
    public void run(CommandContext context) throws CommandException {
        Entity target = context.nextArg(Entity.class);
        target.damage(target.getHealth() + 1);
        context.respond("Entity %d was killed".formatted(target.getId()));
    }
}
