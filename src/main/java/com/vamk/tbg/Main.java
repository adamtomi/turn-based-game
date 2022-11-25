package com.vamk.tbg;

import com.vamk.tbg.config.Config;
import com.vamk.tbg.config.Keys;
import com.vamk.tbg.game.Game;
import com.vamk.tbg.game.GameState;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.ui.GameWindow;
import com.vamk.tbg.util.LogUtil;
import com.vamk.tbg.util.SerialUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        new Bootstrap().launch();
    }

    private static final class Bootstrap {
        private static final Logger LOGGER = LogUtil.getLogger(Bootstrap.class);
        private final Config config;
        private final Game game;
        private final GameWindow window;

        private Bootstrap() {
            SignalDispatcher dispatcher = new SignalDispatcher();
            this.config = new Config();
            this.game = new Game(dispatcher, this.config);
            this.window = new GameWindow(dispatcher, this::handleForceShutdown);
        }

        private void launch() {
            try {
                this.config.load();
            } catch (IOException ex) {
                LOGGER.severe("Failed to load configuration, exiting");
                ex.printStackTrace();
                System.exit(-1);
            }

            attemptRestore();
            this.game.launch();
            this.window.dispose();
        }

        private void attemptRestore() {
            File file = new File(this.config.get(Keys.BACKUP_LOCATION));
            if (!file.exists()) return;

            try {
                GameState state = SerialUtil.readObject(GameState.class, file);
                this.game.importState(state);
                file.delete();
            } catch (ClassNotFoundException | IOException ex) {
                LOGGER.severe("Failed to read game state from file");
                ex.printStackTrace();
            }
        }

        private void handleForceShutdown() {
            LOGGER.info("Force shutdown initiated, writing game state to file");
            GameState state = this.game.exportState();
            try {
                SerialUtil.writeObject(state, new File(this.config.get(Keys.BACKUP_LOCATION)));
            } catch (IOException ex) {
                LOGGER.severe("Failed to write game state to file");
                ex.printStackTrace();
            }
        }
    }
}
