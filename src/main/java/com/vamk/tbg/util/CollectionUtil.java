package com.vamk.tbg.util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CollectionUtil {

    private CollectionUtil() {}

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Function<V, K> keyMapper, V... values) {
        return Stream.of(values).collect(Collectors.toMap(keyMapper, Function.identity()));
    }
}
