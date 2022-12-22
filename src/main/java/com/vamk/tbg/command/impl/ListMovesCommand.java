package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Argument;
import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.command.CommandException;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.combat.Move;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class ListMovesCommand extends Command {

    @Inject
    public ListMovesCommand() {
        super(
                "ls-mov",
                "List moves of the specified entity",
                new Argument("target", "The entity whose moves should be listed")
        );
    }

    @Override
    public void run(CommandContext context) throws CommandException {
        Entity target = context.nextArg(Entity.class);
        String moves = target.getMoves().stream()
                .map(Move::getId)
                .collect(Collectors.joining(", "));
        context.respond("Entity %d's moves: %s".formatted(target.getId(), moves));
    }
}
