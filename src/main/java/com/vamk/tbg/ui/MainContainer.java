package com.vamk.tbg.ui;

import com.vamk.tbg.signal.SignalDispatcher;

import javax.swing.JFrame;
import javax.swing.JSeparator;
import java.awt.BorderLayout;

public class MainContainer extends JFrame {

    public MainContainer(SignalDispatcher dispatcher) {
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout());
        add(new GameContainer(dispatcher), BorderLayout.NORTH);
        add(new JSeparator(), BorderLayout.CENTER);
        add(new TableContainer(dispatcher), BorderLayout.SOUTH);
        setVisible(true);
    }
}
