package com.vamk.tbg.signal;

@FunctionalInterface
public interface SignalHandler<S extends Signal> {

    void handle(S signal);
}
