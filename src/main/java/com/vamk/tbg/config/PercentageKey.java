package com.vamk.tbg.config;

public final class PercentageKey extends AbstractConfigKey<Double> {

    public PercentageKey(String path) {
        super(path);
    }

    @Override
    public Double map(String value) {
        double mapped = Double.parseDouble(value);
        if (mapped > 100 || mapped < 0) throw new IllegalArgumentException("Value %f for key %s is not in range. [0-100]".formatted(mapped, getPath()));
        return mapped / 100.0D;
    }
}
