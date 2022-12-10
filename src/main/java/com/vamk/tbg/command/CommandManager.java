package com.vamk.tbg.command;

import com.vamk.tbg.command.impl.DamageCommand;
import com.vamk.tbg.command.impl.HealCommand;
import com.vamk.tbg.command.impl.KillCommand;
import com.vamk.tbg.command.impl.ListCommand;
import com.vamk.tbg.command.impl.RemoveEffectCommand;
import com.vamk.tbg.command.impl.SetEffectCommand;
import com.vamk.tbg.command.mapper.ArgumentMapper;
import com.vamk.tbg.command.mapper.EntityMapper;
import com.vamk.tbg.command.mapper.IntMapper;
import com.vamk.tbg.command.mapper.StatusEffectMapper;
import com.vamk.tbg.game.Game;
import com.vamk.tbg.util.CollectionUtil;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.LogFormatter;

import java.util.Arrays;
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
     * should always get printed to the console.
     */
    private static final Logger LOGGER = LogUtil.withFormatter(Logger.getAnonymousLogger(), new LogFormatter("%s\n", (format, record) -> format.formatted(record.getMessage())));
    private final Map<Class<?>, ArgumentMapper<?>> knownMappers;
    private final Map<String, Command> knownCommands;
    private boolean listening = true;

    public CommandManager(Game game) {
        this.knownMappers = CollectionUtil.mapOf(
                ArgumentMapper::type,
                new EntityMapper(game),
                new IntMapper(),
                new StatusEffectMapper()
        );
        this.knownCommands = CollectionUtil.mapOf(
                Command::getName,
                new DamageCommand(),
                new HealCommand(),
                new KillCommand(),
                new ListCommand(game),
                new RemoveEffectCommand(),
                new SetEffectCommand()
        );
    }

    public void listen() {
        LogUtil.getLogger(CommandManager.class).info("Listening for commands");
        try (Scanner in = new Scanner(System.in)) {
            while (this.listening) {
                String commandLine = in.nextLine();
                parseAndRun(commandLine);
            }
        }
    }

    public void stop() {
        this.listening = false;
    }

    private void parseAndRun(String commandLine) {
        Queue<String> args = Arrays.stream(commandLine.split(" "))
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedList::new));

        String name = args.poll();
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

    private void printHelp() {
        LOGGER.info("----------------------------------------");
        LOGGER.info("The list of available commands is as follows:\n");
        this.knownCommands.values().forEach(x -> LOGGER.info("%s -> %s".formatted(x.getName(), x.getDescription())));
        LOGGER.info("----------------------------------------");
    }
}
