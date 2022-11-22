package com.vamk.tbg;

import com.vamk.tbg.game.Game;
import com.vamk.tbg.ui.MainContainer;

public class Main {

    public static void main(String[] args) {
        Game game = new Game();
        new MainContainer(game);
        game.launch();
        System.exit(0);
    }
}
