package com.vamk.tbg.ui;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;
import com.vamk.tbg.util.Tickable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.util.List;
import java.util.stream.Collectors;

public class TableContainer extends JPanel implements Tickable {
    private static TableContainer instance;
    private final Game game;

    public TableContainer(Game game) {
        this.game = game;
        setVisible(true);

        instance = this;
    }

    public void init() {
        String[] columns = { "Entity ID", "Health", "Hostile", "Effects" };
        String[][] data = new String[6][4];

        List<Entity> entities = this.game.getEntities();
        for (int i = 0; i < 6; i++) {
            Entity entity = entities.get(i);
            data[i][0] = String.valueOf(entity.getId());
            data[i][1] = String.valueOf(entity.getHealth().get());
            data[i][2] = entity.isHostile() ? "Yes" : "No";
            data[i][3] = entity.getEffects().entrySet()
                    .stream()
                    .map(x -> "%s-%d".formatted(x.getKey().name(), x.getValue()))
                    .collect(Collectors.joining(", "));

            int finalI = i;
            // Make sure to update the table
            entity.getHealth().watch((o, n) -> data[finalI][1] = String.valueOf(entity.getHealth().get()));
        }

        JTable table = new JTable(data, columns);
        table.setBounds(30, 40, 200, 300);
        table.setSize(300, 400);

        JScrollPane sp = new JScrollPane(table);
        add(sp);
    }

    public static TableContainer getInstance() {
        if (instance == null) throw new IllegalStateException("Instance has not yet been initialized");

        return instance;
    }

    @Override
    public void tick() {
        updateUI();
    }
}
