package com.vamk.tbg.ui;

import javax.swing.JLabel;
import java.awt.Font;

public class JText extends JLabel {

    public JText(String text) {
        super(text);
        int fontSize = getFont().getSize();
        setFont(new Font("Tahoma", Font.PLAIN, fontSize));
    }
}
