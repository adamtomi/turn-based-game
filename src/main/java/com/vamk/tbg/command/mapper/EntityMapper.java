package com.vamk.tbg.command.mapper;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;

/**
 * A mapper that converts strings into {@link Entity} objects.
 */
public class EntityMapper implements ArgumentMapper<Entity> {
    private final Game game;

    public EntityMapper(Game game) {
        this.game = game;
    }

    @Override
    public Class<Entity> type() {
        return Entity.class;
    }

    /**
     * Users are expected to specify a number which is then
     * treated as the ID of the entity. If the argument is
     * not a valid number or there's no entity with that ID,
     * an exception is thrown.
     */
    @Override
    public Entity map(String arg) throws ArgumentException {
        try {
            int id = Integer.parseInt(arg);
            return this.game.getEntities().stream()
                    .filter(x -> x.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ArgumentException(this, arg));
        } catch (NumberFormatException ex) {
            throw new ArgumentException(this, arg);
        }
    }
}
