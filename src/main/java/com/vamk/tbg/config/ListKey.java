package com.vamk.tbg.config;

import java.util.Arrays;
import java.util.List;

public abstract class ListKey<T> extends AbstractConfigKey<List<T>> {
    // This value disables size checks
    private static final int NONE = -1;
    private static final String SEPARATOR = ",";
    private final int expectedSize;

    public ListKey(String path, int expectedSize) {
        super(path);
        this.expectedSize = expectedSize;
    }

    public ListKey(String path) {
        this(path, NONE);
    }

    @Override
    public List<T> map(String value) {
        List<String> values = Arrays.stream(value.split(SEPARATOR)).map(String::trim)
                .toList();

        int size = values.size();
        if (size != this.expectedSize && expectedSize != NONE) throw new IllegalArgumentException("Expected %d elements, got %d".formatted(this.expectedSize, size));

        return values.stream().map(this::mapSingle)
                .toList();
    }

    public abstract T mapSingle(String value);
}
