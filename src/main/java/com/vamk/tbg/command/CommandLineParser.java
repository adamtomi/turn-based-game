package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.ArgumentMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CommandLineParser {
    private final Map<Class<?>, ArgumentMapper<?>> knownMappers;
    private final Map<String, Command> knownCommands;

    public CommandLineParser() {
        this.knownMappers = new HashMap<>();
        this.knownCommands = new HashMap<>();
    }

    public void parseAndRun(String commandLine) throws CommandException {
        if (commandLine.isBlank()) throw unrecognizedCommand();

        Queue<String> parsed = Arrays.stream(commandLine.split(" "))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));

        String name = parsed.poll();
        if (name == null) throw unrecognizedCommand();

        Command command = this.knownCommands.get(name);
        if (command == null) throw unrecognizedCommand();

        CommandContext context = new CommandContext(this.knownMappers, parsed);
        command.run(context);
    }

    private CommandException unrecognizedCommand() {
        return new CommandException("Unrecognized command. Type 'help' for help.");
    }

    private CommandException syntaxException(Command command) {
        return new CommandException(getFullHelp(command));
    }

    private String getFullHelp(Command command) {
        StringJoiner syntaxBuilder = new StringJoiner(" ").add(command.getName());
        StringJoiner argBuilder = new StringJoiner("\n");

        for (Argument arg : command.getArguments()) {
            String argName = arg.getName().toUpperCase();

            syntaxBuilder.add("<%s>".formatted(argName));
            argBuilder.add("%s -> %s".formatted(argName, arg.getDescription()));
        }

        StringJoiner builder = new StringJoiner("\n");
        builder.add(syntaxBuilder.toString());
        builder.add(command.getDescription() + "\n");
        builder.add(argBuilder.toString());

        return builder.toString();
    }
}
