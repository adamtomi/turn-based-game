package com.vamk.tbg.game;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record GameState(List<EntitySnapshot> entities, int cursor) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7970394955414240184L;
}
