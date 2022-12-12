package com.vamk.tbg.config;

import java.util.function.Function;

/**
 * Config keys are expected to turn string
 * arguments found in the config file into
 * actually usable values.
 */
public interface ConfigKey<T> {

    /**
     * The path where the value is stored
     * at in the config.
     */
    String getPath();

    /**
     * This method will turn the given string
     * argument into something else, which is
     * of the correct type. Depending on the
     * actual implementation, exceptions might
     * be thrown if the conversion fails.
     *
     * @param value The raw value
     */
    T map(String value);

    /**
     * This method will store an already mapped
     * value in the key. The cache is permanent,
     * meaning there's no expiry on the cached
     * values.
     *
     * @param value The parsed value to be stored
     */
    void cache(T value);

    /**
     * Returns the cached value or null if
     * nothing was cached.
     */

    T getCached();

    /**
     * Returns a config key that converts strings to
     * ints.
     *
     * @param path The path
     * @return A new config key
     * @see SimpleConfigKey
     */
    static ConfigKey<Integer> intKey(String path) {
        return new SimpleConfigKey<>(path, Integer::parseInt);
    }

    /**
     * Returns a config key that doesn't
     * do any modification to the raw string
     * value.
     *
     * @param path The path
     * @return A new config key
     * @see SimpleConfigKey
     */
    static ConfigKey<String> stringKey(String path) {
        return new SimpleConfigKey<>(path, Function.identity());
    }

    /**
     * Returns a config key that converts strings
     * to booleans.
     *
     * @param path The path
     * @return A new config key
     * @see SimpleConfigKey
     */
    static ConfigKey<Boolean> boolKey(String path) {
        return new SimpleConfigKey<>(path, Boolean::parseBoolean);
    }
}
