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

/**
 * This container is responsible for updating
 * the individual EntityData classes upon
 * certain signals.
 */
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

    /**
     * Once the game is ready, all entities will get their own data container.
     */
    private void onGameReady(GameReadySignal signal) {
        boolean flip = true;
        for (Entity entity : signal.getEntities()) {
            EntityData data = new EntityData(entity, flip);
            this.entityData.put(entity.getId(), data);
            add(data.getPanel());
            flip = !flip;
        }
    }

    /**
     * Upon entity death, the correct container is updated
     * to reflect this (in this case it gets grayed out).
     */
    private void onEntityDeath(EntityDeathSignal signal) {
        Entity entity = signal.getEntity();
        EntityData data = this.entityData.get(entity.getId());
        data.died();
    }

    /**
     * Update the displayed health.
     */
    private void onEntityHealthChanged(EntityHealthChangedSignal signal) {
        Entity entity = signal.getEntity();
        EntityData data = this.entityData.get(entity.getId());
        data.updateHealth();
    }

    /**
     * Update the displayed effects.
     */
    private void onEffectsUpdated(EffectsUpdatedSignal signal) {
        Entity entity = signal.getEntity();
        EntityData data = this.entityData.get(entity.getId());
        data.updateEffects();
    }
}
