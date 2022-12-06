package com.vamk.tbg.command;

import java.util.List;

final class CommandImpl implements Command {
    private final String name;
    private final String description;
    private final List<Argument> arguments;
    private final CommandExecutable executor;

    CommandImpl(String name, String description, List<Argument> arguments, CommandExecutable executor) {
        this.name = name;
        this.description = description;
        this.arguments = arguments;
        this.executor = executor;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<Argument> getArguments() {
        return List.copyOf(this.arguments);
    }

    @Override
    public void execute(CommandContext context) throws CommandException {
        this.executor.execute(context);
    }

    @Override
    public String toString() {
        return "Command { name=%s, description=%s, arguments=%s }".formatted(this.name, this.description, this.arguments);
    }
}
