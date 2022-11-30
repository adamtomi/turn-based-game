package com.vamk.tbg.signal;

import com.vamk.tbg.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SignalDispatcher {
    private static final Logger LOGGER = LogUtil.getLogger(SignalDispatcher.class);
    private final Map<Class<? extends Signal>, List<SignalHandler<? extends Signal>>> handlers;

    public SignalDispatcher() {
        this.handlers = new HashMap<>();
    }

    public <S extends Signal> void subscribe(Class<S> type, SignalHandler<S> handler) {
        if (!this.handlers.containsKey(type)) this.handlers.put(type, new ArrayList<>());

        this.handlers.get(type).add(handler);
        LOGGER.info("Registering signal handler %s for type %s".formatted(handler, type.getName()));
    }

    public <S extends Signal> S awaitSignal(Class<S> type) {
        AwaitingSignalHandler<S> handler = new AwaitingSignalHandler<>();
        subscribe(type, handler);
        return handler.await();
    }

    @SuppressWarnings("unchecked")
    public void dispatch(Signal signal) {
        LOGGER.info("Dispatching signal %s".formatted(signal));
        List<SignalHandler<? extends Signal>> handlers = this.handlers.get(signal.getClass());
        if (handlers == null || handlers.isEmpty()) return;

        for (Iterator<SignalHandler<? extends Signal>> iter = handlers.iterator(); iter.hasNext();) {
            SignalHandler<Signal> handler = (SignalHandler<Signal>) iter.next();
            handler.handle(signal);
            // Remove awaiting handlers
            if (handler instanceof AwaitingSignalHandler<Signal>) iter.remove();
        }
    }
}
