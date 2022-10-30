package com.vamk.tbg.util;

import java.util.List;
import java.util.Random;

public final class CollectionUtil {
    private static final Random RANDOM = new Random();

    private CollectionUtil() {}

    public static <T> void randomize(List<T> list) {
        int length = list.size();
        for (int i = 0; i < length; i++) {
            T tmp = list.get(i);
            int randIdx = RANDOM.nextInt(i, length);

            list.set(i, list.get(randIdx));
            list.set(randIdx, tmp);
        }
    }

    public static <T> T chooseRandom(List<T> list) {
        int randIdx = RANDOM.nextInt(list.size());
        return list.get(randIdx);
    }
}
