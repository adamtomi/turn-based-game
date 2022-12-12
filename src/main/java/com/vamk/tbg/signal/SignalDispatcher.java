package com.vamk.tbg.signal;

import com.vamk.tbg.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A simple dispatcher. Other objects can register listeners
 * (aka subscribe) to certain signals, and when a signal is
 * dispatched, all subscribers are notified.
 */
public class SignalDispatcher {
    private static final Logger LOGGER = LogUtil.getLogger(SignalDispatcher.class);
    private final Map<Class<? extends Signal>, List<SignalHandler<? extends Signal>>> handlers;

    public SignalDispatcher() {
        this.handlers = new HashMap<>();
    }

    /**
     * Subscribe to a signal with the provided handler
     *
     * @param type The type of signal
     * @param handler The actual handler
     */
    public <S extends Signal> void subscribe(Class<S> type, SignalHandler<S> handler) {
        if (!this.handlers.containsKey(type)) this.handlers.put(type, new ArrayList<>());

        this.handlers.get(type).add(handler);
        LOGGER.info("Registering signal handler %s for type %s".formatted(handler, type.getName()));
    }

    /**
     * Await a certain signal. This method will block
     * until a signal of the specified type is dispatched.
     *
     * @param type The type of the signal
     * @return The signal instance
     */
    //TODO this method should be replaced to awt/javafx await
    // event functionality
    public <S extends Signal> S awaitSignal(Class<S> type) {
        AwaitingSignalHandler<S> handler = new AwaitingSignalHandler<>();
        subscribe(type, handler);
        return handler.await();
    }

    /**
     * Dispatch a signal. All subscribers will be notified
     * in the order they subscribe to this signal type.
     * Awaiting signal handlers will be removed from
     * the known subscribers.
     */
    //TODO refactor this to get rid of the unchecked cast.
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
