package com.vamk.tbg.config;

import java.util.function.Function;

/**
 * This config key implementation expects a function
 * that turns strings into type T objects. It's meant
 * to be used with simple functions (such as Integer::parseInt),
 * hence it's called simple. For more complicated implementations,
 * it is recommended to extend {@link AbstractConfigKey} instead.
 */
public class SimpleConfigKey<T> extends AbstractConfigKey<T> {
    private final Function<String, T> mapper;

    public SimpleConfigKey(String path, Function<String, T> mapper) {
        super(path);
        this.mapper = mapper;
    }

    @Override
    public T map(String value) {
        return this.mapper.apply(value);
    }
}
