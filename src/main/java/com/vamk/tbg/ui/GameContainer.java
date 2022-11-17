package com.vamk.tbg.ui;

import com.vamk.tbg.game.Game;

import javax.swing.JFrame;

public class GameContainer extends JFrame {
    private final ButtonContainer btnContainer;

    public GameContainer(Game game) {
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        add((this.btnContainer = new ButtonContainer(game)));
        setVisible(true);
    }

    public void init() {
        // this.btnContainer.init();
    }
}
