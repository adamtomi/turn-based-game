package com.vamk.tbg.util;

import java.util.concurrent.CompletableFuture;

public class Awaitable<T> {
    private CompletableFuture<T> future;

    public Awaitable() {
        this.future = new CompletableFuture<>();
    }

    public void complete(T value) {
        this.future.complete(value);
    }

    public T await() {
        try {
            // Await this future. Once this#complete is called (which completes this future)
            // this function will proceed
            return this.future.join();
        } finally {
            // Reset internal state
            this.future = new CompletableFuture<>();
        }
    }
}
