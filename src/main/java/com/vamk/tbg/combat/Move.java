package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.MoveContext;

import java.io.Serializable;

public interface Move extends Serializable {

    String getId();

    boolean isAttack();

    boolean isApplicableTo(Entity entity);

    void perform(MoveContext context);
}
