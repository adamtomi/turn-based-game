package com.vamk.tbg.signal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignalDispatcher {
    private final Map<Class<? extends Signal>, List<SignalHandler<? extends Signal>>> handlers;

    public SignalDispatcher() {
        this.handlers = new HashMap<>();
    }

    public <S extends Signal> void subscribe(Class<S> type, SignalHandler<S> handler) {
        if (!this.handlers.containsKey(type)) this.handlers.put(type, new ArrayList<>());

        this.handlers.get(type).add(handler);
    }

    @SuppressWarnings("unchecked")
    public void dispatch(Signal signal) {
        List<SignalHandler<? extends Signal>> handlers = this.handlers.get(signal.getClass());
        if (handlers == null || handlers.isEmpty()) return;

        for (SignalHandler<? extends Signal> handler : handlers) {
            ((SignalHandler<Signal>) handler).handle(signal);
        }
    }
}
