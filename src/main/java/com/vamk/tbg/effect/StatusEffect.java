package com.vamk.tbg.effect;

public final class StatusEffect {
    /* Negative status effects */
    public static final int BLEEDING = 1;
    public static final int CONFUSED = 2;
    public static final int FROZEN = 4;

    /* Postivie status effects */
    public static final int CAFFEINATED = 8;
    public static final int LIFESTEAL = 16;
    public static final int REGENERATION = 32;

    private StatusEffect() {}
}
