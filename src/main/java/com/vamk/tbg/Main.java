package com.vamk.tbg;

import com.vamk.tbg.game.Game;
import com.vamk.tbg.game.GameState;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.ui.GameWindow;
import com.vamk.tbg.util.IOUtil;
import com.vamk.tbg.util.LogUtil;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        new Bootstrap().launch();
    }

    private static final class Bootstrap {
        private static final Logger LOGGER = LogUtil.getLogger(Bootstrap.class);
        private final Game game;
        private final GameWindow window;

        private Bootstrap() {
            SignalDispatcher dispatcher = new SignalDispatcher();
            this.game = new Game(dispatcher);
            this.window = new GameWindow(dispatcher, this::handleForceShutdown);
        }

        private void launch() {
            this.game.launch();
            this.window.dispose();
        }

        private void handleForceShutdown() {
            LOGGER.info("Force shutdown initiated, writing game state to file");
            GameState state = this.game.exportState();
            try {
                IOUtil.writeObject(state, GameState.FILEPATH);
            } catch (IOException ex) {
                LOGGER.severe("Failed to write game state to file");
                ex.printStackTrace();
            }
        }
    }
}
