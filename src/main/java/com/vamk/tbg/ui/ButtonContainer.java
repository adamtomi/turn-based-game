package com.vamk.tbg.ui;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.combat.Move;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityDeathSignal;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import com.vamk.tbg.signal.impl.GameReadySignal;
import com.vamk.tbg.signal.impl.UserReadySignal;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.GridLayout;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The user can interact with the game through
 * this container. All move and entity buttons
 * are contained within this panel.
 */
public class ButtonContainer extends JPanel {
    @Serial
    private static final long serialVersionUID = -1848838910296723317L;
    private final SignalDispatcher dispatcher;
    private final Config config;
    private final Map<Entity, JButton> entityButtons;
    private final List<MoveButton> moveButtons;
    private Entity currentEntity;
    private Move move;

    @Inject
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

    /**
     * Create all necessary buttons for all entities
     * and moves once the game is ready.
     */
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

    /**
     * When an entity dies, the button belonging to
     * that entity gets disabled.
     */
    private void onEntityDeath(EntityDeathSignal signal) {
        Entity entity = signal.getEntity();
        JButton button = this.entityButtons.remove(entity);
        if (button == null) return;

        button.setEnabled(false);
    }

    /**
     * Every time an entity plays, the move buttons
     * need to be updated to give a visual indication
     * of the available moves. If the entity is not
     * controlled by the user, the buttons get disabled.
     */
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

        // Enable all entity buttons again
        if (userControlled) this.entityButtons.values().forEach(x -> x.setEnabled(true));
    }

    /**
     * The game flow is that the move is selected first, and
     * only after that will the entity get selected. So if
     * there's no move selected at this point, don't do
     * anything. Otherwise, a new {@link UserReadySignal}
     * is dispatched.
     *
     * @see UserReadySignal
     */
    private void entityButtonClicked(Entity target) {
        if (this.move == null) return;

        this.dispatcher.dispatch(new UserReadySignal(target, this.move));
    }

    /**
     * The state of the entity buttons (whether they're enabled
     * or disabled) depends on the currently selected move.
     * Some moves aren't applicable to all entities, so
     * the entity buttons should update accordingly.
     */
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
