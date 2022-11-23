package com.vamk.tbg.ui;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EffectsUpdatedSignal;
import com.vamk.tbg.signal.impl.EntityHealthChangedSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;
import com.vamk.tbg.util.Tickable;

import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableContainer extends JPanel implements Tickable {
    private final SignalDispatcher dispatcher;

    public TableContainer(SignalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        dispatcher.subscribe(GameReadySignal.class, this::onGameReady);
        dispatcher.subscribe(EntityPlaysSignal.class, x -> tick());
        setVisible(true);
    }

    private void onGameReady(GameReadySignal signal) {
        String[] columns = { "Entity ID", "Health", "Hostile", "Effects" };
        String[][] data = new String[6][4];

        List<Entity> entities = signal.getEntities();
        for (int i = 0; i < 6; i++) {
            Entity entity = entities.get(i);
            data[i][0] = String.valueOf(entity.getId());
            data[i][1] = String.valueOf(entity.getHealth());
            data[i][2] = entity.isHostile() ? "Yes" : "No";
            data[i][3] = getEffectsEntry(entity);

            int finalI = i;
            // Make sure to update the table
            this.dispatcher.subscribe(EntityHealthChangedSignal.class, x -> data[finalI][1] = String.valueOf(entity.getHealth()));
            this.dispatcher.subscribe(EffectsUpdatedSignal.class, x -> data[finalI][3] = getEffectsEntry(entity));
        }

        JTable table = new JTable(data, columns);
        table.getColumn("Effects").setMinWidth(500);
        table.setPreferredSize(new Dimension(1500, 500));
        table.setRowSelectionAllowed(false);
        add(table);
        table.setVisible(true);
    }

    private String getEffectsEntry(Entity entity) {
        Map<StatusEffect, Integer> effects = entity.getEffects();
        if (effects.isEmpty()) return "-";

        return entity.getEffects().entrySet()
                .stream()
                .map(x -> "%s-%d".formatted(x.getKey().name(), x.getValue()))
                .collect(Collectors.joining(", "));
    }

    @Override
    public void tick() {
        updateUI();
    }
}
