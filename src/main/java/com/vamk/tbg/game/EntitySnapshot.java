package com.vamk.tbg.game;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * A snapshot of an entity's current state. Just the important data
 * gets serialized this way.
 */
/*
 * Marking certain fields transient could achieve roughly the
 * same thing, however moves started to become problematic
 * as they kept having more and more members in them. It's
 * easier to serialize just the name of the moves and look
 * them up later.
 */
public record EntitySnapshot(int id, boolean hostile, int health, int maxHealth, List<String> moves) implements Serializable {
    @Serial
    private static final long serialVersionUID = -4684032326053564157L;
}
