package com.vamk.tbg.effect;

public enum StatusEffect {

    /* Negative effects */

    /**
     * Bleeding entities lose a chunk of their HP
     * every turn.
     */
    BLEEDING(3, false),
    /**
     * Confused entities attack their teammates
     * instead of their enemies.
     */
    CONFUSED(2, false),
    /**
     * Frozen entities lose a turn.
     */
    FROZEN(2, false),

    /* Positive effects */

    /**
     * Caffeinated entities can attack twice in
     * a single round.
     */
    CAFFEINATED(1, true),
    /**
     * Entities affected by lifesteal can gain some HP
     * if they deal damage to hostile entities.
     */
    LIFESTEAL(2, true),
    /**
     * Entities will regenerate some HP each round.
     */
    REGENERATION(3, true),
    ;

    private final int rounds;
    private final boolean positive;

    StatusEffect(int rounds, boolean positive) {
        this.rounds = rounds;
        this.positive = positive;
    }

    /**
     * Determines how many rounds this effect
     * lasts for.
     *
     * @return int
     */
    public int getRounds() {
        return this.rounds;
    }
}
