package com.vamk.tbg.ui;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EffectsUpdatedSignal;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityHealthChangedSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class EntityDataContainer extends JPanel {
    @Serial
    private static final long serialVersionUID = -7455891810519039109L;
    private final Map<Integer, EntityData> entityData;

    public EntityDataContainer(SignalDispatcher dispatcher) {
        dispatcher.subscribe(GameReadySignal.class, this::onGameReady);
        dispatcher.subscribe(EntityDeathSignal.class, this::onEntityDeath);
        dispatcher.subscribe(EntityPlaysSignal.class, x -> updateUI());
        dispatcher.subscribe(EntityHealthChangedSignal.class, this::onEntityHealthChanged);
        dispatcher.subscribe(EffectsUpdatedSignal.class, this::onEffectsUpdated);
        this.entityData = new HashMap<>();

        setVisible(true);
        setLayout(new GridLayout(1, 6));
    }

    private void onGameReady(GameReadySignal signal) {
        boolean flip = true;
        for (Entity entity : signal.getEntities()) {
            EntityData data = new EntityData(entity, flip);
            this.entityData.put(entity.getId(), data);
            add(data.getPanel());
            flip = !flip;
        }
    }

    private void onEntityDeath(EntityDeathSignal signal) {
        Entity entity = signal.getEntity();
        EntityData data = this.entityData.get(entity.getId());
        data.died();
    }

    private void onEntityHealthChanged(EntityHealthChangedSignal signal) {
        Entity entity = signal.getEntity();
        EntityData data = this.entityData.get(entity.getId());
        data.updateHealth();
    }

    private void onEffectsUpdated(EffectsUpdatedSignal signal) {
        Entity entity = signal.getEntity();
        EntityData data = this.entityData.get(entity.getId());
        data.updateEffects();
    }
}
