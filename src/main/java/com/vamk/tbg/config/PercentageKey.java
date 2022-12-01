package com.vamk.tbg.config;

public final class PercentageKey extends AbstractConfigKey<Double> {

    public PercentageKey(String path) {
        super(path);
    }

    @Override
    public Double map(String value) {
        double mapped = Double.parseDouble(value);
        return mapped / 100.0D;
    }
}
