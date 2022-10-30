package com.vamk.tbg.combat;

import com.vamk.tbg.game.MoveContext;

public interface Move {

    String getId();

    boolean isAttack();

    void perform(MoveContext context);
}
