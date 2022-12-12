package com.vamk.tbg.util;

import java.util.List;
import java.util.Random;

public final class RandomUtil {
    private static final Random RANDOM = new Random();

    private RandomUtil() {}

    /**
     * Randomizes the provided list by replacing
     * elements at random indexes with each other.
     *
     * @param list The list to randomize
     */
    public static <T> void randomize(List<T> list) {
        int length = list.size();
        for (int i = 0; i < length; i++) {
            T tmp = list.get(i);
            int randIdx = RANDOM.nextInt(i, length);

            list.set(i, list.get(randIdx));
            list.set(randIdx, tmp);
        }
    }

    /**
     * Returns a random element of the provided
     * list.
     */
    public static <T> T pickRandom(List<T> list) {
        int randIdx = RANDOM.nextInt(list.size());
        return list.get(randIdx);
    }

    // TODO Finally replace this...
    public static boolean chance(int chance) {
        if (chance == 100) return true;
        if (chance == 0) return false;

        return RANDOM.nextInt(1, 100) <= chance;
    }

    /**
     * Generates a random value between the two
     * provided numbers.
     *
     * @param lbound The lower bound
     * @param ubound The upper bound
     * @see java.util.Random#nextInt(int, int)
     */
    public static int random(int lbound, int ubound) {
        return RANDOM.nextInt(lbound, ubound + 1);
    }
}
