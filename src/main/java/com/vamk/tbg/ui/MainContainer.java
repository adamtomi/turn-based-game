package com.vamk.tbg.ui;

import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import java.awt.Font;
import java.awt.GridLayout;

public class MainContainer extends JFrame {
    private final JLabel entityLabel;

    public MainContainer(SignalDispatcher dispatcher) {
        this.entityLabel = new JLabel();
        this.entityLabel.setFont(new Font("Serif", Font.BOLD, 20)); // Change the default font size
        this.entityLabel.setHorizontalAlignment(JLabel.CENTER);
        this.entityLabel.setVerticalAlignment(JLabel.CENTER);

        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new GridLayout(5,  1));
        add(new GameContainer(dispatcher));
        add(new JSeparator());
        add(this.entityLabel);
        add(new JSeparator());
        add(new TableContainer(dispatcher));
        setVisible(true);
        dispatcher.subscribe(EntityPlaysSignal.class, this::onEntityPlays);
    }
    
    private void onEntityPlays(EntityPlaysSignal signal) {
        this.entityLabel.setText("Currently playing: Entity %d".formatted(signal.getEntity().getId()));
    }
}
