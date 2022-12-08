package com.vamk.tbg.command;

import com.vamk.tbg.command.mapper.ArgumentMapper;

import java.util.Map;
import java.util.Queue;

public class CommandContext {
    private final Map<Class<?>, ArgumentMapper<?>> knownMappers;
    private final Queue<String> args;

    public CommandContext(Map<Class<?>, ArgumentMapper<?>> knownMappers, Queue<String> args) {
        this.knownMappers = knownMappers;
        this.args = args;
    }

    public int remaining() {
        return this.args.size();
    }

    public <T> T nextArg(Class<T> type) throws CommandException {
        ArgumentMapper<?> mapper = this.knownMappers.get(type);
        if (mapper == null) throw new CommandException("Unrecognized argument type: %s".formatted(type.getName()));

        String arg = this.args.remove();
        Object result = mapper.map(arg);
        if (!type.isInstance(result)) throw new CommandException("Argument %s cannot be converted to type %s".formatted(arg, type.getName()));

        return type.cast(result);
    }

    // TODO actually implement this
    public void respond(String message) {

    }
}
