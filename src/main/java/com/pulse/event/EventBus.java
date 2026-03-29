package com.pulse.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {

    private static class Handler implements Comparable<Handler> {
        final Object instance;
        final Method method;
        final int priority;

        Handler(Object instance, Method method, int priority) {
            this.instance = instance;
            this.method = method;
            this.priority = priority;
        }

        @Override
        public int compareTo(Handler other) {
            return Integer.compare(other.priority, this.priority);
        }
    }

    private final Map<Class<? extends Event>, List<Handler>> registry = new ConcurrentHashMap<>();

    public void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventListener.class)) continue;
            if (method.getParameterTypes().length != 1) continue;

            Class<?> param = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(param)) continue;

            method.setAccessible(true);
            EventListener annotation = method.getAnnotation(EventListener.class);

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventType = (Class<? extends Event>) param;

            registry.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                    .add(new Handler(listener, method, annotation.priority()));

            List<Handler> handlers = registry.get(eventType);
            Collections.sort(handlers);
        }
    }


    public void unregister(Object listener) {
        for (List<Handler> handlers : registry.values()) {
            handlers.removeIf(h -> h.instance == listener);
        }
    }

    public <T extends Event> T post(T event) {
        List<Handler> handlers = registry.get(event.getClass());
        if (handlers == null || handlers.isEmpty()) return event;

        for (Handler handler : handlers) {
            try {
                handler.method.invoke(handler.instance, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return event;
    }
}
