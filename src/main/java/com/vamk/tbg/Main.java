package com.vamk.tbg;

import com.vamk.tbg.command.CommandManager;
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
import java.util.concurrent.CompletableFuture;
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
        private final CommandManager cmdManager;

        private Bootstrap() {
            SignalDispatcher dispatcher = new SignalDispatcher();
            this.config = new Config();
            this.game = new Game(dispatcher, this.config);
            this.window = new GameWindow(dispatcher, this.config, this::handleForceShutdown);
            this.cmdManager = new CommandManager(this.game);
        }

        /**
         * Tries to load the configuration. If that fails, exit.
         * After that, register the command listener (if dev mode
         * is enabled), then launch the game. Once the game loop
         * finishes, dismantle the window and the command manager
         * and exit.
         */
        private void launch() {
            try {
                this.config.load();
            } catch (Exception ex) {
                LOGGER.severe("Failed to load configuration, exiting");
                ex.printStackTrace();
                System.exit(-1);
            }

            // Setup command listener on a separate thread
            if (this.config.get(Keys.DEV_MODE)) CompletableFuture.runAsync(this.cmdManager::listen);

            attemptRestore();
            this.game.launch();
            this.window.dispose();
            this.cmdManager.stop();
        }

        /**
         * Tries to read a previous game state from the configured
         * file. If the file doesn't exist, return. Otherwise,
         * load the state and import it into {@link this#game}.
         */
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

        /**
         * This method will be called if the opened JFrame is closed.
         * The current game state will be written to the configured
         * file. If the file already exists, the contents of it
         * will be replaced.
         */
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
