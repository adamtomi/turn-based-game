package com.vamk.tbg.util;

import java.util.List;
import java.util.Random;

public final class RandomUtil {
    private static final Random RANDOM = new Random();

    private RandomUtil() {}

    public static <T> void randomize(List<T> list) {
        int length = list.size();
        for (int i = 0; i < length; i++) {
            T tmp = list.get(i);
            int randIdx = RANDOM.nextInt(i, length);

            list.set(i, list.get(randIdx));
            list.set(randIdx, tmp);
        }
    }

    public static <T> T pickRandom(List<T> list) {
        int randIdx = RANDOM.nextInt(list.size());
        return list.get(randIdx);
    }

    public static boolean chance(int chance) {
        if (chance == 100) return true;
        if (chance == 0) return false;

        return RANDOM.nextInt(1, 100) <= chance;
    }
}
