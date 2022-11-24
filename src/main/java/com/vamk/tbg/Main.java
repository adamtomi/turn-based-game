package com.vamk.tbg;

import com.vamk.tbg.game.Game;
import com.vamk.tbg.signal.SignalDispatcher;
import com.vamk.tbg.ui.GameWindow;

public class Main {

    public static void main(String[] args) {
        SignalDispatcher dispatcher = new SignalDispatcher();
        GameWindow container = new GameWindow(dispatcher);
        Game game = new Game(dispatcher);
        game.launch();
        container.dispose();
    }
}
