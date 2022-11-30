package com.vamk.tbg.signal;

import java.util.concurrent.CountDownLatch;

public class AwaitingSignalHandler<S extends Signal> implements SignalHandler<S> {
    private final CountDownLatch latch;
    private S signal;

    public AwaitingSignalHandler() {
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void handle(S signal) {
        this.signal = signal;
        this.latch.countDown();
    }

    public S await() {
        try {
            this.latch.await();
            return this.signal;
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
