package com.vamk.tbg.config;

import java.util.Arrays;
import java.util.List;

final class MovePresetsKey extends AbstractConfigKey<List<List<String>>> {
    private static final String PREFIX = "(";
    private static final String SUFFIX = ")";

    public MovePresetsKey(String path) {
        super(path);
    }

    @Override
    public List<List<String>> map(String value) {
        String[] entries = value.split(",");
        return Arrays.stream(entries)
                .map(this::processInvididual)
                .toList();
    }

    private List<String> processInvididual(String entry) {
        if (entry.startsWith(PREFIX)) entry = entry.substring(1);
        if (entry.endsWith(SUFFIX)) entry = entry.substring(0, entry.length() - 2);

        return Arrays.stream(entry.split(" "))
                .map(String::trim)
                .toList();
    }
}
