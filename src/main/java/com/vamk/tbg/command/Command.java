package com.vamk.tbg.command;

import java.util.List;

/**
 * Describes a command that has a name, description,
 * arguments and can be executed by the user.
 *
 * @see Argument
 * @see CommandContext
 */
public abstract class Command implements CommandPart {
    private final String name;
    private final String description;
    private final List<Argument> arguments;

    protected Command(String name, String description, Argument... arguments) {
        this.name = name;
        this.description = description;
        this.arguments = List.of(arguments);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public List<Argument> getArguments() {
        return this.arguments;
    }

    public abstract void run(CommandContext context) throws CommandException;
}
