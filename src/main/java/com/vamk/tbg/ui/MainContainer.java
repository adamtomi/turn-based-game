package com.vamk.tbg.ui;

import com.vamk.tbg.signal.SignalDispatcher;

import javax.swing.JFrame;
import javax.swing.JSeparator;
import java.awt.GridLayout;

public class MainContainer extends JFrame {

    public MainContainer(SignalDispatcher dispatcher) {
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        // setLayout(new BorderLayout(10, 10));
        setLayout(new GridLayout(3,  1));
        add(new GameContainer(dispatcher)); // , BorderLayout.NORTH
        add(new JSeparator());
        add(new TableContainer(dispatcher)); // , BorderLayout.SOUTH
        setVisible(true);
    }
}
