package com.vamk.tbg.game;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record EntitySnapshot (int id, boolean hostile, int health, int maxHealth, List<String> moves) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
