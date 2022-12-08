package com.vamk.tbg.config;

import java.util.Arrays;
import java.util.List;

final class MovePresetsKey extends ListKey<List<String>> {
    private static final String PREFIX = "[";
    private static final String SUFFIX = "]";

    public MovePresetsKey(String path) {
        super(path);
    }

    @Override
    public List<String> mapSingle(String value) {
        if (value.startsWith(PREFIX)) value = value.substring(1);
        if (value.endsWith(SUFFIX)) value = value.substring(0, value.length() - 1);

        return Arrays.stream(value.split(" "))
                .map(String::trim)
                .toList();
    }
}
