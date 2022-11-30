package com.vamk.tbg.ui;

import com.vamk.tbg.combat.Move;
import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;
import com.vamk.tbg.signal.impl.UserReadySignal;
import com.vamk.tbg.util.Tickable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.GridLayout;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonContainer extends JPanel implements Tickable {
    @Serial
    private static final long serialVersionUID = -1848838910296723317L;
    private final SignalDispatcher dispatcher;
    private final Config config;
    private final Map<Integer, JButton> entityButtons;
    private final List<JButton> moveButtons;
    private int moveIdx;

    public ButtonContainer(SignalDispatcher dispatcher, Config config) {
        this.dispatcher = dispatcher;
        this.config = config;
        this.moveIdx = 3;
        this.entityButtons = new HashMap<>();
        this.moveButtons = new ArrayList<>();

        dispatcher.subscribe(GameReadySignal.class, this::onGameReady);
        dispatcher.subscribe(EntityPlaysSignal.class, this::onEntityPlays);
        dispatcher.subscribe(EntityDeathSignal.class, this::onEntityDeath);

        setLayout(new GridLayout(3, 1, 10, 10));
        setVisible(true);
    }

    private void onGameReady(GameReadySignal signal) {
        JPanel entityPanel = new JPanel();
        for (Entity entity : signal.getEntities()) {
            JButton button = new JButton("%s entity %d".formatted(entity.isHostile() ? "Hostile" : "Friendly", entity.getId()));

            button.addActionListener(event -> this.dispatcher.dispatch(new UserReadySignal(entity, this.moveIdx)));

            button.setVisible(true);
            entityPanel.add(button);
            this.entityButtons.put(entity.getId(), button);
        }

        add(entityPanel);
        JSeparator sep = new JSeparator();
        add(sep);

        int moveCount = this.config.get(Keys.MOVE_COUNT);
        JPanel movePanel = new JPanel();
        for (int i = 0; i < moveCount; i++) {
            JButton button = new JButton("Move %d".formatted(i));

            int finalI = i;
            button.addActionListener(e -> ButtonContainer.this.moveIdx = finalI);

            button.setEnabled(false);
            button.setVisible(true);
            this.moveButtons.add(button);
            movePanel.add(button);
        }

        add(movePanel);
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
        updateUI();
    }
}
