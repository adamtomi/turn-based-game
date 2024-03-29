package com.vamk.tbg.ui;

import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.signal.impl.EntityPlaysSignal;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serial;

/**
 * The main game window containing all other UI containers.
 * It also listens to window close events to allow a custom
 * hook to run before exiting.
 */
public class GameWindow extends JFrame implements WindowListener {
    @Serial
    private static final long serialVersionUID = 3210313597488494598L;
    private final JLabel entityLabel;
    private final Runnable shutdownHook;

    @AssistedInject
    public GameWindow(SignalDispatcher dispatcher,
                      ButtonContainer btnContainer,
                      EntityDataContainer edContainer,
                      @Assisted Runnable shutdownHook) {
        this.entityLabel = new JLabel();
        this.entityLabel.setFont(new Font("Tahoma", Font.BOLD, 20)); // Change the default font size
        this.entityLabel.setHorizontalAlignment(JLabel.CENTER);
        this.entityLabel.setVerticalAlignment(JLabel.CENTER);
        this.shutdownHook = shutdownHook;

        addWindowListener(this);

        setFocusable(true);
        setTitle("Turn Based Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLayout(new GridLayout(3,  1));
        add(btnContainer);
        add(this.entityLabel);
        add(edContainer);
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

    @AssistedFactory
    public interface Factory {

        GameWindow create(Runnable shutdownHook);
    }
}
