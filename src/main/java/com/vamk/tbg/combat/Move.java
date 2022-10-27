package com.vamk.tbg.combat;

import com.vamk.tbg.game.Entity;

public interface Move {

    void perform(Entity source, Entity target);
}
