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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.GridLayout;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonContainer extends JPanel {
    @Serial
    private static final long serialVersionUID = -1848838910296723317L;
    private final SignalDispatcher dispatcher;
    private final Config config;
    private final Map<Entity, JButton> entityButtons;
    private final List<MoveButton> moveButtons;
    private Entity currentEntity;
    private Move move;

    public ButtonContainer(SignalDispatcher dispatcher, Config config) {
        this.dispatcher = dispatcher;
        this.config = config;
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

            button.addActionListener(event -> this.entityButtonClicked(entity));

            button.setVisible(true);
            entityPanel.add(button);
            this.entityButtons.put(entity, button);
        }

        add(entityPanel);
        JSeparator sep = new JSeparator();
        add(sep);

        int moveCount = this.config.get(Keys.MOVE_COUNT);
        JPanel movePanel = new JPanel();
        for (int i = 0; i < moveCount; i++) {
            MoveButton button = new MoveButton(i, this::moveButtonClicked);
            this.moveButtons.add(button);
            movePanel.add(button.unwrap());
        }

        add(movePanel);
    }

    private void onEntityDeath(EntityDeathSignal signal) {
        Entity entity = signal.getEntity();
        JButton button = this.entityButtons.remove(entity);
        if (button == null) return;

        button.setEnabled(false);
    }

    private void onEntityPlays(EntityPlaysSignal signal) {
        Entity entity = signal.getEntity();
        boolean userControlled = signal.isUserControlled();

        this.currentEntity = entity;
        updateUI();

        for (int i = 0; i < this.moveButtons.size(); i++) {
            MoveButton button = this.moveButtons.get(i);
            Move move = entity.getMoves().get(i);
            button.setText(move.getId());

            button.setEnabled(userControlled);
        }
    }

    private void entityButtonClicked(Entity target) {
        if (this.move == null) return;

        this.dispatcher.dispatch(new UserReadySignal(target, this.move));
    }

    private void moveButtonClicked(int idx) {
        Move move = this.currentEntity.getMoves().get(idx);
        this.move = move;
        for (Map.Entry<Entity, JButton> entry : this.entityButtons.entrySet()) {
            /*
             * Prevent user from performing attacks on friendly entities
             * as well as using moves that aren't applicable to the
             * targeted entity.
             */
            boolean btnStatus = (this.currentEntity.isEnemyOf(entry.getKey()) == move.isAttack())
                    && (move.isApplicableTo(entry.getKey()));
            entry.getValue().setEnabled(btnStatus);
        }
    }
}
