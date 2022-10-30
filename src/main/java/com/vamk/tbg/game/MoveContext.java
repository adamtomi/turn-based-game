package com.vamk.tbg.game;

import java.util.List;

public record MoveContext(Entity source, Entity target, List<Entity> allEntities) {}
