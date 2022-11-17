package com.vamk.tbg;

import com.vamk.tbg.game.Game;
import com.vamk.tbg.ui.GameContainer;

public class Main {

    public static void main(String[] args) {
        Game game = new Game();
        GameContainer container = new GameContainer(game);
        container.init();
        game.launch();
        game.destroy();
        System.exit(0);
    }
}
