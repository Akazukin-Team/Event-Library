package org.akazukin.event.method;

import org.akazukin.event.IListenable;

public interface IEventMethod<T> {
    Class<T> getEventType();

    IListenable getEventClass();

    void call(T event);
}
