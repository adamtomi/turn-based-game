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
    private final boolean harmful;

    StatusEffect(int rounds, boolean harmful) {
        this.rounds = rounds;
        this.harmful = harmful;
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

    /**
     * Determines whether this effect is
     * harmful to the entity it's applied to.
     *
     * @return boolean
     */
    public boolean isHarmful() {
        return this.harmful;
    }
}
