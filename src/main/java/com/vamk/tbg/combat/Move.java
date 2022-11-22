package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;

public interface Move {

    String getId();

    boolean isAttack();

    @Deprecated
    boolean isTargeted();

    boolean isApplicableTo(Entity entity);

    void perform(MoveContext context);
}
