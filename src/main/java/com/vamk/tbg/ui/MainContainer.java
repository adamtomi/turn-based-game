package com.vamk.tbg.ui;

import com.vamk.tbg.game.Game;

import javax.swing.JFrame;

public class MainContainer extends JFrame {

    public MainContainer(Game game) {
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        add(new GameContainer(game));
        setVisible(true);
    }
}
