package org.akazukin.event.hook;

import org.akazukin.event.method.IEventMethod;
import org.akazukin.event.target.IEventCondition;

public interface IEventHook<T> {
    IEventMethod<T> getMethod();

    IEventCondition getCondition();
}
