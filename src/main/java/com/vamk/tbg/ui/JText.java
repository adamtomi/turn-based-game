package com.vamk.tbg.ui;

import javax.swing.JLabel;
import java.awt.Font;
import java.io.Serial;

/**
 * A simple class that changes the default font.
 */
public class JText extends JLabel {
    @Serial
    private static final long serialVersionUID = 6926232278909736002L;

    public JText(String text) {
        super(text);
        int fontSize = getFont().getSize();
        setFont(new Font("Tahoma", Font.PLAIN, fontSize));
    }
}
