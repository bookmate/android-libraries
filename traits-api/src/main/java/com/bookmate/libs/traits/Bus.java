package com.bookmate.libs.traits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides type-safe communication. Consists of event and request systems.
 * <p>
 * {@link #event(Object)} is used to notify about some event and to pass some data via it's parameter. There can be any number of listeners of event of certain type
 * <p>
 * {@link #requestData(DataRequest)} is used to retrieve some data. There can be ONLY ONE listener of request of certain type.
 */
@SuppressWarnings("UnusedDeclaration")
public class Bus {

    protected final Map<Class<?>, DataRequestListener> dataRequestListeners = new HashMap<>();
    protected final Map<Class<?>, List<EventListener>> eventListeners = new HashMap<>();

    /**
     * it's better not to call this method directly, but to use {@link com.bookmate.libs.traits.DataRequest} instead
     */
    public <RESULT, R extends DataRequest<RESULT>> void register(Class<R> requestClass, DataRequestListener<RESULT, R> listener) {
        dataRequestListeners.put(requestClass, listener);
    }

    /**
     * it's better not to call this method directly, but to use {@link com.bookmate.libs.traits.Event} instead
     */
    public <E> void register(Class<E> eventClass, EventListener<E> listener) {
        List<EventListener> listeners = eventListeners.get(eventClass);
        if (listeners == null) {
            listeners = new ArrayList<>();
            eventListeners.put(eventClass, listeners);
        }
        listeners.add(listener);
    }

    public <RESULT, R extends DataRequest<RESULT>> void unregister(Class<R> requestClass) {
        dataRequestListeners.remove(requestClass);
    }

    public <E> void unregister(Class<E> eventClass, EventListener<E> listener) {
        List<EventListener> listeners = eventListeners.get(eventClass);
        if (listeners != null) listeners.remove(listener);
    }


    ///

    @SuppressWarnings("unchecked")
    public <RESULT, R extends DataRequest<RESULT>> RESULT requestData(R dataRequest) {
        final DataRequestListener<RESULT, R> processor = dataRequestListeners.get(dataRequest.getClass());
        return processor == null ? dataRequest.defaultResult() : processor.process(dataRequest);
    }

    @SuppressWarnings("unchecked")
    public <E> void event(E event) {
        final List<EventListener> listeners = eventListeners.get(event.getClass());
        if (listeners != null)
            for (EventListener listener : listeners)
                listener.process(event);
    }

    ///

    public abstract static class DataRequest<R> {
        protected R defaultResult() {
            return null;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public abstract static class BooleanRequest extends DataRequest<Boolean> {

        @Override
        protected Boolean defaultResult() {
            return false;
        }
    }

    public interface DataRequestListener<RESULT, R extends DataRequest<RESULT>> {
        RESULT process(R request);
    }

    ///

    public interface EventListener<E> {
        void process(E event);
    }

}
