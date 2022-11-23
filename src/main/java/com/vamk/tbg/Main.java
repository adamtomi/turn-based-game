package com.vamk.tbg;

import com.vamk.tbg.game.Game;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.ui.MainContainer;

public class Main {

    public static void main(String[] args) {
        SignalDispatcher dispatcher = new SignalDispatcher();
        Game game = new Game(dispatcher);
        new MainContainer(dispatcher);
        game.launch();
        System.exit(0);
    }
}
