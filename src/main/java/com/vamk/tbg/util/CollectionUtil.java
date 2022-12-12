package com.vamk.tbg.util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO once dagger is in place, this class is not needed anymore.
public final class CollectionUtil {

    private CollectionUtil() {}

    /**
     * Convert a bunch of type V values into a map.
     *
     * @see com.vamk.tbg.command.CommandManager
     * @see com.vamk.tbg.game.Game
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Function<V, K> keyMapper, V... values) {
        return Stream.of(values).collect(Collectors.toMap(keyMapper, Function.identity()));
    }
}
