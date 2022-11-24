package com.vamk.tbg;

import com.vamk.tbg.game.Game;
import com.vamk.tbg.game.GameState;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.ui.GameWindow;
import com.vamk.tbg.util.IOUtil;
import com.vamk.tbg.util.LogUtil;

import java.io.IOException;

public class Main {
    private final Game game;
    private final GameWindow window;

    public Main() {
        SignalDispatcher dispatcher = new SignalDispatcher();
        this.game = new Game(dispatcher);
        this.window = new GameWindow(dispatcher, this::handleForceShutdown);
    }

    public static void main(String[] args) {
        new Main().launch();
    }

    private void launch() {
        this.game.launch();
        this.window.dispose();
    }

    private void handleForceShutdown() {
        GameState state = this.game.exportState();
        try {
            IOUtil.writeObject(state, GameState.FILEPATH);
        } catch (IOException ex) {
            LogUtil.getLogger(Main.class).severe("Failed to write gamestate to file");
            ex.printStackTrace();
        }
    }
}
