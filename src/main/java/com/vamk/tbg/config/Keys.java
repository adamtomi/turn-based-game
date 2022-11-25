package com.vamk.tbg.config;

public final class Keys {
    public static final ConfigKey<Integer> ENTITY_COUNT = ConfigKey.intKey("game.entity-count");
    public static final ConfigKey<Integer> ENTITY_MAX_HEALTH = ConfigKey.intKey("game.entity-max-health");
    public static final ConfigKey<Integer> MOVE_COUNT = ConfigKey.intKey("game.move-count");
    public static final MovePresetsKey MOVE_PRESETS = new MovePresetsKey("game.move-presets");

    public static final ConfigKey<String> BACKUP_LOCATION = ConfigKey.stringKey("game-state-backup.location");

    private Keys() {}
}
