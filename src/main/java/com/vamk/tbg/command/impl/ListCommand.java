package com.vamk.tbg.command.impl;

import com.vamk.tbg.command.Command;
import com.vamk.tbg.command.CommandContext;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;

import java.util.stream.Collectors;

public class ListCommand extends Command {
    private final Game game;

    public ListCommand(Game game) {
        super(
                "list",
                "List all entities"
        );
        this.game = game;
    }

    @Override
    public void run(CommandContext context) {
        String entities = this.game.getEntities().stream()
                .map(Entity::toString)
                .collect(Collectors.joining("\n"));

        context.respond("Listing all entities... \n%s".formatted(entities));
    }
}
