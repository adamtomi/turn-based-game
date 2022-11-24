package com.vamk.tbg.ui;

import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class GameWindow extends JFrame implements WindowListener {
    private final JLabel entityLabel;
    private final Runnable shutdownHook;

    public GameWindow(SignalDispatcher dispatcher, Runnable shutdownHook) {
        this.entityLabel = new JLabel();
        this.entityLabel.setFont(new Font("Serif", Font.BOLD, 20)); // Change the default font size
        this.entityLabel.setHorizontalAlignment(JLabel.CENTER);
        this.entityLabel.setVerticalAlignment(JLabel.CENTER);
        this.shutdownHook = shutdownHook;

        addWindowListener(this);

        setFocusable(true);
        setTitle("Turn Based Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new GridLayout(3,  1));
        add(new ButtonContainer(dispatcher));
        add(this.entityLabel);
        add(new EntityDataContainer(dispatcher));
        setVisible(true);
        dispatcher.subscribe(EntityPlaysSignal.class, this::onEntityPlays);
    }

    private void onEntityPlays(EntityPlaysSignal signal) {
        this.entityLabel.setText("Currently playing: Entity %d".formatted(signal.getEntity().getId()));
    }

    // Wonderful... So many useful methods.
    @Override
    public void windowOpened(WindowEvent event) {}

    @Override
    public void windowClosing(WindowEvent event) {
        this.shutdownHook.run();
    }

    @Override
    public void windowClosed(WindowEvent event) {}

    @Override
    public void windowIconified(WindowEvent event) {}

    @Override
    public void windowDeiconified(WindowEvent event) {}

    @Override
    public void windowActivated(WindowEvent event) {}

    @Override
    public void windowDeactivated(WindowEvent event) {}
}
