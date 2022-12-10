package com.vamk.tbg.ui;

import javax.swing.JButton;
import java.util.function.IntConsumer;

public class MoveButton {
    private final JButton button;

    public MoveButton(int index, IntConsumer clickHandler) {
        this.button = new JButton("Move %d".formatted(index));
        this.button.addActionListener(x -> clickHandler.accept(index));
        this.button.setEnabled(true);
        this.button.setVisible(true);
    }

    public JButton unwrap() {
        return this.button;
    }

    public void setText(String text) {
        this.button.setText(text);
    }

    public void setEnabled(boolean enabled) {
        this.button.setEnabled(enabled);
    }
}
