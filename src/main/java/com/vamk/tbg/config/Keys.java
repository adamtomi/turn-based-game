package com.vamk.tbg.config;

/**
 * Stores all config key constants.
 */
public final class Keys {
    /* General settings */
    public static final ConfigKey<Boolean> DEV_MODE = ConfigKey.boolKey("general.dev-mode");

    /* Game settings */
    public static final ConfigKey<Integer> ENTITY_COUNT = ConfigKey.intKey("game.entity-count");
    public static final ConfigKey<Integer> ENTITY_MAX_HEALTH = ConfigKey.intKey("game.entity-max-health");
    public static final ConfigKey<Integer> MOVE_COUNT = ConfigKey.intKey("game.move-count");
    public static final MovePresetsKey MOVE_PRESETS = new MovePresetsKey("game.move-presets");
    public static final ConfigKey<String> BACKUP_LOCATION = ConfigKey.stringKey("game-state-backup.location");

    /* Effect settings */
    public static final ConfigKey<Double> BLEEDING_MODIFIER = new PercentageKey("effect.bleeding-modifier");
    public static final ConfigKey<Double> REGEN_MODIFIER = new PercentageKey("effect.regen-modifier");

    /* Move settings */
    public static final MapKey<Integer> BUFF_CHANCES = new MapKey.Simple<>("move.buff", 3, Integer::parseInt);
    public static final MapKey<Integer> DEBUFF_CHANCES = new MapKey.Simple<>("move.debuff", 3, Integer::parseInt);
    public static final MapKey<Integer> DAMAGE_CONFIG = new MapKey.Simple<>("move.damage", 4, Integer::parseInt);
    public static final MapKey<Integer> HEAL_ALL = new MapKey.Simple<>("move.heal-all", 2, Integer::parseInt);
    public static final MapKey<Integer> HEAL = new MapKey.Simple<>("move.heal", 2, Integer::parseInt);
    public static final MapKey<Integer> SPLASH_DAMAGE = new MapKey.Simple<>("move.splash-damage", 3, Integer::parseInt);

    private Keys() {}
}
