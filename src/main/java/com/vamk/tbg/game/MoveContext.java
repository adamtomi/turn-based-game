package com.vamk.tbg.game;

import java.util.List;

/**
 * A context for all moves. It has a reference to the currently
 * playing entity, the target as well as all living entities.
 *
 * @see com.vamk.tbg.game.combat.Move#perform(MoveContext)
 */
public record MoveContext(Entity source, Entity target, List<Entity> allEntities) {}
