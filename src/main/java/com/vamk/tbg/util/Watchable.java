package com.vamk.tbg.util;

import java.util.function.BiConsumer;

public class Watchable<T> {
    private T value;
    private BiConsumer<T, T> changeHandler;

    public Watchable(T initial) {
        this.value = initial;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        T old = this.value;
        this.value = value;
        if (this.changeHandler != null) this.changeHandler.accept(old, value);
    }

    public void watch(BiConsumer<T, T> changeHandler) {
        this.changeHandler = changeHandler;
    }

    @Override
    public String toString() {
        return "Watchable { value=%s }".formatted(this.value);
    }
}
