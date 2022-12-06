package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.StatusEffectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class CommandLineParser {
    private static final String OPTIONAL_FORMAT = "[%s]";
    private static final String REQUIRED_FORMAT = "<%s>";
    private final Map<String, Command> knownCommands;

    public CommandLineParser() {
        this.knownCommands = new HashMap<>();
    }

    public void parse(String commandLine) throws CommandException {
        if (commandLine.isBlank()) throw unrecognizedCommand();

        Queue<String> parsed = Arrays.stream(commandLine.split(" "))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));

        String name = parsed.poll();
        if (name == null) throw unrecognizedCommand();

        Command command = this.knownCommands.get(name);
        if (command == null) throw unrecognizedCommand();

        CommandContext context = parseArguments(command, parsed);
        command.execute(context);
    }

    private CommandContext parseArguments(Command command, Queue<String> args) throws CommandException {
        for (Argument argument : command.getArguments()) {
            String arg = args.poll();
            if (arg == null) {
                if (argument.required()) {
                    throw syntaxException(command);
                } else {
                    break;
                }
            }

            try {
                Object mapped = argument.mapper().map(arg);
                // Todo store mapped in a context
            } catch (CommandException ex) {
                if (argument.required()) throw syntaxException(command);
            }
        }

        throw new UnsupportedOperationException();
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
            syntaxBuilder.add(formatArgument(arg));
            argBuilder.add("%s -> %s".formatted(arg.name().toUpperCase(), arg.description()));
        }

        StringJoiner builder = new StringJoiner("\n");
        builder.add(syntaxBuilder.toString());
        builder.add(command.getDescription() + "\n");
        builder.add(argBuilder.toString());

        return builder.toString();
    }

    private String formatArgument(Argument argument) {
        String format = argument.required() ? REQUIRED_FORMAT : OPTIONAL_FORMAT;
        return format.formatted(argument.name().toUpperCase());
    }

    public static void main(String[] args) {
        CommandLineParser parser = new CommandLineParser();
        Command command = Command.forName("set-eff")
                .withDescription("Apply the specified effect to the specified entity")
                .withArguments(
                        Argument.required("target").useMapper(x -> "hello").withDescription("The entity to apply the effect to"),
                        Argument.required("effect").useMapper(StatusEffectMapper.INSTANCE).withDescription("The effect to apply to the entity"),
                        Argument.optional("rounds").useMapper(x -> 10).withDescription("The number of rounds the effect should last for")
                )
                .execute(context -> {})
                .build();

        String help = parser.getFullHelp(command);

        System.out.println("\n\n\n");
        System.out.println("-------------------------");
        System.out.println("\n");
        System.out.println(help);
        System.out.println("\n");
        System.out.println("-------------------------");
        System.out.println("\n\n\n");
    }
}
