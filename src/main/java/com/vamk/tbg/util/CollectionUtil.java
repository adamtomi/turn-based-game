package com.vamk.tbg.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtil {

    private CollectionUtil() {}

    /**
     * Convert a bunch of type V values into a map.
     *
     * @see com.vamk.tbg.command.CommandManager
     * @see com.vamk.tbg.game.Game
     */
    public static <K, V, C extends Collection<V>> Map<K, V> mapOf(Function<V, K> keyMapper, C values) {
        return values.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    }
}
