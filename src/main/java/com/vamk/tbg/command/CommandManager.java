package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.ArgumentMapper;
import com.vamk.tbg.di.qualifier.CommandSet;
import com.vamk.tbg.di.qualifier.MapperSet;
import com.vamk.tbg.util.CollectionUtil;
import com.vamk.tbg.util.LogFormatter;
import com.vamk.tbg.util.LogUtil;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class is reponsible for listening to user
 * interactions, taking the given user input
 * and trying to run a command based on it.
 * If that's not possible, an error message will
 * be displayed to the user explaining what went
 * wrong.
 */
public class CommandManager {
    /*
     * Don't use LogUtil#getLogger for this logger, as those loggers
     * might log into files instead of the console, however these messages
     * should always be printed to the console (so that the user can
     * actually see them). Also, the formatting of said loggers might
     * be different, this needs to be as clutter-free as possible.
     */
    private static final Logger LOGGER = LogUtil.withFormatter(Logger.getAnonymousLogger(), new LogFormatter("%s\n", (format, record) -> format.formatted(record.getMessage())));
    /* Format for required arguments */
    private static final String REQUIRED_FORMAT = "<%s>";
    /* Format for optional arguments */
    private static final String OPTIONAL_FORMAT = "[%s]";
    /* Store all known mappers in a map */
    private final Map<Class<?>, ArgumentMapper<?>> knownMappers;
    /* Store all commands in a map */
    private final Map<String, Command> knownCommands;
    /* This state will be changed by this#listen and this#stop */
    private boolean listening;

    @Inject
    public CommandManager(@MapperSet Set<ArgumentMapper<?>> mappers,
                          @CommandSet Set<Command> commands) {
        this.knownMappers = CollectionUtil.mapOf(ArgumentMapper::type, mappers);
        this.knownCommands = CollectionUtil.mapOf(Command::getName, commands);
    }

    /**
     * Set up the listener.
     */
    public void listen() {
        this.listening = true;
        LogUtil.getLogger(CommandManager.class).info("Listening for commands");
        try (Scanner in = new Scanner(System.in)) {
            while (this.listening) {
                String commandLine = in.nextLine();
                parseAndRun(commandLine);
            }
        }
    }

    /**
     * Sets this#listening to false thus stopping
     * the loop inside this#listen.
     */
    public void stop() {
        this.listening = false;
    }

    /**
     * Parses the commandline into a queue of strings
     * and then tries to run the command that was
     * specified. If the input is empty, no command
     * was found with the given name or the execution
     * failed for some other reason, an error message
     * is displayed to the user
     *
     * @param commandLine The user input
     */
    private void parseAndRun(String commandLine) {
        Queue<String> args = Arrays.stream(commandLine.split(" "))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));

        String name = args.poll(); // poll returns null if there are no more elements in the queue
        if (name == null) {
            printHelp();
            return;
        }

        Command command = this.knownCommands.get(name);
        if (command == null) {
            printHelp();
            return;
        }

        CommandContext context = new CommandContext(this.knownMappers, args, x -> LOGGER.info("[%s]: %s".formatted(command.getName(), x)));
        try {
            command.run(context);
        } catch (CommandException ex) {
            LOGGER.info(ex.getMessage());
            LOGGER.info(getFullHelp(command));

        }
    }

    /**
     * Returns an informative help message based on
     * the given command. The name, description
     * and arguments (including names and descriptions)
     * are included.
     *
     * @param command The command
     * @return The constructed message
     */
    private String getFullHelp(Command command) {
        StringJoiner syntaxBuilder = new StringJoiner(" ").add(command.getName());
        StringJoiner argBuilder = new StringJoiner("\n");

        for (Argument arg : command.getArguments()) {
            String argName = arg.getName().toUpperCase();
            String syntaxPart = (arg.isOptional() ? OPTIONAL_FORMAT : REQUIRED_FORMAT).formatted(argName);
            syntaxBuilder.add(syntaxPart);
            argBuilder.add("%s -> %s".formatted(argName, arg.getDescription()));
        }

        StringJoiner builder = new StringJoiner("\n");
        builder.add(syntaxBuilder.toString());
        builder.add(command.getDescription() + "\n");
        builder.add(argBuilder.toString());

        return builder.toString();
    }

    /**
     * Prints all available command names (and their
     * descriptions) to the console.
     */
    private void printHelp() {
        LOGGER.info("----------------------------------------");
        LOGGER.info("The list of available commands:\n");
        this.knownCommands.values().forEach(x -> LOGGER.info("%s -> %s".formatted(x.getName(), x.getDescription())));
        LOGGER.info("----------------------------------------");
    }
}
