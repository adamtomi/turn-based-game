package com.vamk.tbg.ui;

import com.vamk.tbg.game.Entity;
import com.vamk.tbg.game.Game;
import com.vamk.tbg.util.Awaitable;
import com.vamk.tbg.util.Tickable;
import com.vamk.tbg.util.UserInput;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class ButtonContainer extends JPanel implements Tickable {
    private static ButtonContainer instance;
    private final Awaitable<Entity> entity;
    private final Game game;
    private final Set<JButton> buttons;
    private int moveIdx;

    public ButtonContainer(Game game) {
        this.game = game;
        this.entity = new Awaitable<>();
        this.moveIdx = 3;
        this.buttons = new HashSet<>();

        setVisible(true);
        instance = this;
    }

    public static ButtonContainer getInstance() {
        if (instance == null) throw new IllegalStateException("Instance was not yet set");

        return instance;
    }

    public void init() {
        for (Entity entity : this.game.getEntities()) {
            JButton button = new JButton("Entity %d".formatted(entity.getId()));
            button.setSize(100, 50);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    ButtonContainer.this.entity.complete(entity);
                }
            });

            button.setVisible(true);
            add(button);
        }

        JSeparator sep = new JSeparator();
        sep.setVisible(true);
        add(sep);

        for (int i = 0; i < 4; i++) {
            JButton button = new JButton("Move %d".formatted(i));
            button.setSize(100, 50);

            int finalI = i;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ButtonContainer.this.moveIdx = finalI;
                }
            });

            button.setVisible(true);
            add(button);
        }

    }

    public UserInput readUserInput() {
        return new UserInput(this.entity.await(), this.moveIdx);
    }

    @Override
    public void tick() {
        this.moveIdx = 3;
    }
}
