package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.ArgumentMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommandManager {
    /*
     * Don't use LogUtil for this logger, as those loggers might
     * log into files instead of to console, however, these messages
     * should always be printed to the console.
     */
    private static final Logger LOGGER = Logger.getLogger("[CommandListener]");
    private static final String UNRECOGNIZED_COMMAND = "Unrecognized command. Type 'help' for help.";
    private final Map<Class<?>, ArgumentMapper<?>> knownMappers;
    private final Map<String, Command> knownCommands;
    private boolean listening = true;

    public CommandManager() {
        this.knownMappers = new HashMap<>();
        this.knownCommands = new HashMap<>();
    }

    public void listen() {
        try (Scanner in = new Scanner(System.in)) {
            while (this.listening) {
                String commandLine = in.next();
                parseAndRun(commandLine);
            }
        }
    }

    public void stop() {
        this.listening = false;
    }

    private void parseAndRun(String commandLine) {
        if (commandLine.isBlank()) {
            LOGGER.info(UNRECOGNIZED_COMMAND);
            return;
        }

        Queue<String> args = Arrays.stream(commandLine.split(" "))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));

        String name = args.poll();
        if (name == null) {
            LOGGER.info(UNRECOGNIZED_COMMAND);
            return;
        }

        Command command = this.knownCommands.get(name);
        if (command == null) {
            LOGGER.info(UNRECOGNIZED_COMMAND);
            return;
        }

        CommandContext context = new CommandContext(this.knownMappers, args, x -> LOGGER.info("[%s]: %s".formatted(command.getName(), x)));
        try {
            command.run(context);
        } catch (CommandException ex) {
            LOGGER.warning(ex.getMessage());
            LOGGER.warning("\n");
            LOGGER.warning(getFullHelp(command));

        }
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
