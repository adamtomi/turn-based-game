package com.vamk.tbg;

import com.vamk.tbg.game.Game;
import com.vamk.tbg.util.LogUtil;

import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = LogUtil.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Game game = new Game();
            game.launch();
        } catch (Throwable ex) {
            LOGGER.severe("Caught an exception while the game was running. Exiting...");
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
