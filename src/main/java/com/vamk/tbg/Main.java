package com.vamk.tbg;

import com.vamk.tbg.di.DaggerGameComponent;
import com.vamk.tbg.di.GameComponent;

public class Main {

    public static void main(String[] args) {
        GameComponent component = DaggerGameComponent.create();
        component.bootstrap().launch();
    }
}
