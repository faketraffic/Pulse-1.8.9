package com.pulse.event.events;

import com.pulse.event.Event;

public class EventKey extends Event {

    private final int key;

    public EventKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
