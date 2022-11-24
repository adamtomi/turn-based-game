package com.vamk.tbg.util;

import java.util.concurrent.CountDownLatch;

public class Awaitable<T> {
    private CountDownLatch latch;
    private T value;

    public Awaitable() {
        reset();
    }

    public T await() {
        try {
            this.latch.await();
            return this.value;
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            reset();
        }
    }

    public void complete(T value) {
        this.value = value;
        this.latch.countDown();
    }

    private void reset() {
        this.latch = new CountDownLatch(1);
        this.value = null;
    }
}
