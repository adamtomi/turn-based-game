package com.vamk.tbg.command.mapper;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;

public class EntityMapper implements ArgumentMapper<Entity> {
    private final Game game;

    public EntityMapper(Game game) {
        this.game = game;
    }

    @Override
    public Class<Entity> type() {
        return Entity.class;
    }

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
