package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;

/**
 * Describes a move that can be performed by entities
 * during the game. Some moves are meant to be
 * performed on teammates, others on enemies.
 */
public interface Move {

    /**
     * The ID of the move, must be unique for each
     * implementation. When loading the config, moves
     * will be parsed based on this ID.
     */
    String getId();

    /**
     * Determines whether this move is an attack or not.
     * Attacks can only be performed on enemies, other moves
     * can only be performed on friends (heal or buff moves
     * for instance).
     */
    boolean isAttack();

    /**
     * The default implementation for this (defined in
     * {@link AbstractMove}) is to simply return true,
     * however, some moves override that (for instance
     * the {@link CureMove}).
     *
     * @param entity The entity
     * @return Whether this move is applicable to entity.
     */
    boolean isApplicableTo(Entity entity);

    /**
     * Perform the move logic on based on the
     * specified {@link MoveContext}.
     *
     * @param context The context of this move
     * @see MoveContext
     */
    void perform(MoveContext context);
}
