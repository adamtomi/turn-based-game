package com.vamk.tbg.ui;

import com.vamk.tbg.combat.Move;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;
import com.vamk.tbg.util.Awaitable;
import com.vamk.tbg.util.Tickable;
import com.vamk.tbg.util.UserInput;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameContainer extends JPanel implements Tickable {
    private static GameContainer instance;
    private final Awaitable<Entity> entity;
    private final Game game;
    private final Map<Integer, JButton> entityButtons;
    private final List<JButton> moveButtons;
    private int moveIdx;

    public GameContainer(Game game) {
        this.game = game;
        this.entity = new Awaitable<>();
        this.moveIdx = 3;
        this.entityButtons = new HashMap<>();
        this.moveButtons = new ArrayList<>();

        setVisible(true);
        setLayout(new GridLayout(2, 6));
        instance = this;
    }

    public static GameContainer getInstance() {
        if (instance == null) throw new IllegalStateException("Instance was not yet set");

        return instance;
    }

    public void init() {
        for (Entity entity : this.game.getEntities()) {
            JButton button = new JButton("%s entity %d".formatted(entity.isHostile() ? "Hostile" : "Friendly", entity.getId()));
            button.setPreferredSize(new Dimension(100, 50));

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    GameContainer.this.entity.complete(entity);
                }
            });

            button.setVisible(true);
            add(button, BorderLayout.PAGE_START);
            this.entityButtons.put(entity.getId(), button);
        }

        JSeparator sep = new JSeparator();
        add(sep);

        for (int i = 0; i < 4; i++) {
            JButton button = new JButton("Move %d".formatted(i));
            button.setSize(100, 50);

            int finalI = i;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GameContainer.this.moveIdx = finalI;
                }
            });

            button.setEnabled(false);
            button.setVisible(true);
            this.moveButtons.add(button);
            add(button);
        }

        JSeparator sep1 = new JSeparator();
        add(sep1);

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

    public UserInput readUserInput() {
        return new UserInput(this.entity.await(), this.moveIdx);
    }

    public void updateButtonsFor(Entity entity) {
        for (int i = 0; i < this.moveButtons.size(); i++) {
            JButton button = this.moveButtons.get(i);
            Move move = entity.getMoves().get(i);
            button.setText(move.getId());
        }
    }

    public void enableMoveButtons() {
        this.moveButtons.forEach(btn -> btn.setEnabled(true));
    }

    public void disableMoveButtons() {
        this.moveButtons.forEach(btn -> btn.setEnabled(false));
    }

    @Override
    public void tick() {
        this.moveIdx = 3;
        Set<Integer> dead = this.entityButtons.keySet()
                .stream()
                .filter(x -> this.game.getEntities().stream().noneMatch($ -> $.getId() == x))
                .collect(Collectors.toSet());
        for (Integer i : dead) {
            JButton button = this.entityButtons.get(i);
            button.setVisible(false);
            remove(button);
            this.entityButtons.remove(i);
        }

        updateUI();
    }
}
