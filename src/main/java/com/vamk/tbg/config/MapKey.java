package com.vamk.tbg.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * A key that can convert a string such as
 * key0=value0 key1=value1 key2=value
 * into an actual {@link Map}.
 */
public abstract class MapKey<T> extends AbstractConfigKey<Map<String, T>> {
    /* Separates key-value pairs */
    private static final String SEPARATOR = ",";
    /* Separates a single key and value */
    private static final String KV_SEPARATOR = "=";
    private final int expectedSize;

    public MapKey(String path, int expectedSize) {
        super(path);
        this.expectedSize = expectedSize;
    }

    @Override
    public Map<String, T> map(String value) {
        List<String> values = Arrays.stream(value.split(SEPARATOR)).map(String::trim)
                .toList();

        int size = values.size();
        if (size != this.expectedSize) throw new IllegalArgumentException("Expected %d elements, got %d".formatted(this.expectedSize, size));

        Map<String, T> result = new HashMap<>();
        for (String each : values) {
            String[] data = each.split(KV_SEPARATOR, 2);
            if (data.length != 2) throw new IllegalArgumentException("Invalid map entry %s at %s".formatted(each, getPath()));

            result.put(data[0].trim(), mapSingle(data[1].trim()));
        }

        return result;
    }

    public abstract T mapSingle(String value);

    public static final class Simple<T> extends MapKey<T> {
        private final Function<String, T> mapper;

        public Simple(String path, int expectedSize, Function<String, T> mapper) {
            super(path, expectedSize);
            this.mapper = mapper;
        }

        @Override
        public T mapSingle(String value) {
            return this.mapper.apply(value);
        }
    }
}
