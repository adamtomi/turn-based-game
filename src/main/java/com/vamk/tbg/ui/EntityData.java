package com.vamk.tbg.ui;

import com.vamk.tbg.effect.StatusEffect;
import com.vamk.tbg.game.Entity;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityData {
    private static final Color FRIENDLY_COLOR = new Color(60, 160, 38);
    private static final Color DEFAULT_BG_COLOR = new Color(220, 220, 220);
    private final Entity entity;
    private final JLabel idLabel;
    private final JLabel healthLabel;
    private final JLabel effectsLabel;
    private final JPanel panel;

    public EntityData(Entity entity, boolean highlight) {
        Color textColor = entity.isHostile() ? Color.RED : FRIENDLY_COLOR;
        Color bgColor = highlight ? Color.WHITE : DEFAULT_BG_COLOR;

        this.entity = entity;
        this.idLabel = new JLabel("Entity id: %d".formatted(entity.getId()));
        this.idLabel.setForeground(textColor);
        this.idLabel.setFont(new Font("Serif", Font.BOLD, 15));

        this.healthLabel = new JLabel(getHealthEntry());
        this.healthLabel.setForeground(textColor);
        this.effectsLabel = new JLabel(getEffectsEntry());
        this.effectsLabel.setForeground(textColor);

        this.panel = new JPanel();
        this.panel.setLayout(new GridLayout(3, 1, 20, 0));
        this.panel.setBackground(bgColor);
        Border border = BorderFactory.createLineBorder(Color.DARK_GRAY);
        this.panel.setBorder(border);

        this.panel.add(this.idLabel);
        this.panel.add(this.healthLabel);
        this.panel.add(this.effectsLabel);
    }

    private String getEffects() {
        Map<StatusEffect, Integer> effects = entity.getEffects();
        if (effects.isEmpty()) return "-";

        return this.entity.getEffects().entrySet()
                .stream()
                .map(x -> "%s-%d".formatted(x.getKey().name(), x.getValue()))
                .collect(Collectors.joining(", "));
    }

    private String getEffectsEntry() {
        return "Effects: %s".formatted(getEffects());
    }

    private String getHealthEntry() {
        return "Health: %d/%d".formatted(this.entity.getHealth(), this.entity.getMaxHealth());
    }

    public void updateHealth() {
        this.healthLabel.setText(getHealthEntry());
    }

    public void updateEffects() {
        this.effectsLabel.setText(getEffectsEntry());
    }

    public void died() {
        Color color = Color.DARK_GRAY;
        this.panel.setBackground(color);
    }

    public JPanel getPanel() {
        return this.panel;
    }
}
