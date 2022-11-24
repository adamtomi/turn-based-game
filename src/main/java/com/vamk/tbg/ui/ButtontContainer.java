package com.vamk.tbg.ui;

import com.vamk.tbg.combat.Move;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;
import com.vamk.tbg.util.Awaitable;
import com.vamk.tbg.util.Tickable;
import com.vamk.tbg.util.UserInput;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtontContainer extends JPanel implements Tickable {
    // TODO remove static instance
    private static ButtontContainer instance;
    private final Awaitable<Entity> entity;
    private final Map<Integer, JButton> entityButtons;
    private final List<JButton> moveButtons;
    private int moveIdx;

    public ButtontContainer(SignalDispatcher dispatcher) {
        this.entity = new Awaitable<>();
        this.moveIdx = 3;
        this.entityButtons = new HashMap<>();
        this.moveButtons = new ArrayList<>();

        dispatcher.subscribe(GameReadySignal.class, this::onGameReady);
        dispatcher.subscribe(EntityPlaysSignal.class, this::onEntityPlays);
        dispatcher.subscribe(EntityDeathSignal.class, this::onEntityDeath);

        setVisible(true);
        setLayout(new GridLayout(2, 6, 20, 20));
        instance = this;
    }

    private void onGameReady(GameReadySignal signal) {
        for (Entity entity : signal.getEntities()) {
            JButton button = new JButton("%s entity %d".formatted(entity.isHostile() ? "Hostile" : "Friendly", entity.getId()));
            button.setPreferredSize(new Dimension(100, 50));

            button.addActionListener(event -> ButtontContainer.this.entity.complete(entity));

            button.setVisible(true);
            add(button, BorderLayout.PAGE_START);
            this.entityButtons.put(entity.getId(), button);
        }

        JSeparator sep = new JSeparator();
        sep.setVisible(false);
        add(sep);

        for (int i = 0; i < 4; i++) {
            JButton button = new JButton("Move %d".formatted(i));
            button.setSize(100, 50);

            int finalI = i;
            button.addActionListener(e -> ButtontContainer.this.moveIdx = finalI);

            button.setEnabled(false);
            button.setVisible(true);
            this.moveButtons.add(button);
            add(button);
        }
    }

    private void onEntityPlays(EntityPlaysSignal signal) {
        tick();
        updateButtonsFor(signal.getEntity());
        if (signal.isUserControlled()) enableMoveButtons();
        else disableMoveButtons();
    }

    private void onEntityDeath(EntityDeathSignal signal) {
        Entity entity = signal.getEntity();
        JButton button = this.entityButtons.remove(entity.getId());
        if (button == null) return;

        button.setEnabled(false);
    }

    // TODO remove this method
    public static ButtontContainer getInstance() {
        if (instance == null) throw new IllegalStateException("Instance was not yet set");

        return instance;
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

    public UserInput readUserInput() {
        return new UserInput(this.entity.await(), this.moveIdx);
    }

    @Override
    public void tick() {
        this.moveIdx = 3;
        updateUI();
    }
}
