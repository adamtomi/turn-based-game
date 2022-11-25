package com.vamk.tbg.config;

abstract class AbstractConfigKey<T> implements ConfigKey<T> {
    private final String path;
    private T value;

    AbstractConfigKey(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void cache(T value) {
        this.value = value;
    }

    @Override
    public T getCached() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.path;
    }
}
