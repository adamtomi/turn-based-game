package com.vamk.tbg.game;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    public static final String FILEPATH = "gamestate.dat";
    @Serial
    private static final long serialVersionUID = -7970394955414240184L;
    private final List<EntitySnapshot> entities;
    private final int cursor;

    public GameState(List<EntitySnapshot> entities, int cursor) {
        this.entities = entities;
        this.cursor = cursor;
    }

    public List<EntitySnapshot> getEntities() {
        return this.entities;
    }

    public int getCursor() {
        return this.cursor;
    }
}
