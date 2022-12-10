package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.ArgumentMapper;

import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

public class CommandContext {
    private final Map<Class<?>, ArgumentMapper<?>> knownMappers;
    private final Queue<String> args;
    private final Consumer<String> messageDelivery;

    public CommandContext(Map<Class<?>, ArgumentMapper<?>> knownMappers, Queue<String> args, Consumer<String> messageDelivery) {
        this.knownMappers = knownMappers;
        this.args = args;
        this.messageDelivery = messageDelivery;
    }

    public int remaining() {
        return this.args.size();
    }

    public <T> T nextArg(Class<T> type) throws CommandException {
        ArgumentMapper<?> mapper = this.knownMappers.get(type);
        if (mapper == null) throw new CommandException("Unrecognized argument type: %s".formatted(type.getName()));

        String arg = this.args.remove();
        Object result = mapper.map(arg);
        // This check is here just in case, but mappers
        // are expected to take care of invalid input.
        if (!type.isInstance(result)) throw new RuntimeException("Failed to convert argument %s to type %s".formatted(arg, type.getName()));

        return type.cast(result);
    }

    public void respond(String message) {
        this.messageDelivery.accept(message);
    }
}
