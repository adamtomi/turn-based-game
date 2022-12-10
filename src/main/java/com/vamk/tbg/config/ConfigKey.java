package com.vamk.tbg.config;

import java.util.function.Function;

public interface ConfigKey<T> {

    String getPath();

    T map(String value);

    void cache(T value);

    T getCached();

    static ConfigKey<Integer> intKey(String path) {
        return new SimpleConfigKey<>(path, Integer::parseInt);
    }

    static ConfigKey<String> stringKey(String path) {
        return new SimpleConfigKey<>(path, Function.identity());
    }

    static ConfigKey<Boolean> boolKey(String path) {
        return new SimpleConfigKey<>(path, Boolean::parseBoolean);
    }
}
