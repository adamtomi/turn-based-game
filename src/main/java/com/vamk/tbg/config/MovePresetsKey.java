package com.vamk.tbg.config;

import java.util.Arrays;
import java.util.List;

/**
 * A special key for the "game.move-presets" configuration key.
 * It takes a list of string lists and turns that into a list
 * of move lists. Later, when the game is initialized, it will
 * select random move combinations from this list.
 */
final class MovePresetsKey extends AbstractConfigKey<List<List<String>>> {
    private static final String PREFIX = "[";
    private static final String SUFFIX = "]";

    public MovePresetsKey(String path) {
        super(path);
    }

    @Override
    public List<List<String>> map(String value) {
        String[] entries = value.split(",");
        return Arrays.stream(entries)
                .map(String::trim)
                .map(this::processInvididual)
                .toList();
    }

    /**
     * This method is resposible for converting
     * a single string ( [value0 value1 value2 value3] )
     * into a {@link List}
     */
    private List<String> processInvididual(String entry) {
        if (entry.startsWith(PREFIX)) entry = entry.substring(1);
        if (entry.endsWith(SUFFIX)) entry = entry.substring(0, entry.length() - 1);

        return Arrays.stream(entry.split(" "))
                .map(String::trim)
                .toList();
    }
}
