package com.vamk.tbg.config;

import java.util.function.Function;

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
