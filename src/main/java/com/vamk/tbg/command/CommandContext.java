package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.ArgumentMapper;

import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public class CommandContext {
    /* Store all known mappers in a map for this#nextArg */
    private final Map<Class<?>, ArgumentMapper<?>> knownMappers;
    /* Parsed command arguments */
    private final Queue<String> args;
    /* This consumer is responsible for displaying messages to the user */
    private final Consumer<String> messageDelivery;

    public CommandContext(Map<Class<?>, ArgumentMapper<?>> knownMappers, Queue<String> args, Consumer<String> messageDelivery) {
        this.knownMappers = knownMappers;
        this.args = args;
        this.messageDelivery = messageDelivery;
    }

    /**
     * Returns the number of remaining arguments.
     */
    public int remaining() {
        return this.args.size();
    }

    /**
     * Returns the next available argument. The
     * correct mapper will be invoked to turn
     * the string argument into whatever type
     * was specified. If the operation fails,
     * an exception is thrown.
     *
     * @param type The type the argument should be
     *             turned into
     * @throws CommandException If the specified
     * type is not known or there aren't any
     * arguments left.
     */
    public <T> T nextArg(Class<T> type) throws CommandException {
        ArgumentMapper<?> mapper = this.knownMappers.get(type);
        if (mapper == null) throw new CommandException("Unrecognized argument type: %s".formatted(type.getName()));

        String arg = this.args.poll();
        if (arg == null) throw new CommandException("Invalid command syntax, at least one more %s is required.".formatted(type.getName()));

        Object result = mapper.map(arg);
        // This check is here just in case, but mappers
        // are expected to take care of invalid input.
        if (!type.isInstance(result)) throw new RuntimeException("Failed to convert argument %s to type %s".formatted(arg, type.getName()));

        return type.cast(result);
    }

    /**
     * Send a message to the user.
     *
     * @param message The message to display.
     */
    public void respond(String message) {
        this.messageDelivery.accept(message);
    }
}
