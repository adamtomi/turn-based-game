package com.vamk.tbg.effect;

public abstract class StatusEffectHolder {
    private int effects = 0;

    public void setEffect(int effect) {
        this.effects |= effect;
    }

    public void delEffect(int effect) {
        this.effects = this.effects &~ effect;
    }

    public boolean hasEffect(int effect) {
        return (this.effects & effect) != 0;
    }
}
