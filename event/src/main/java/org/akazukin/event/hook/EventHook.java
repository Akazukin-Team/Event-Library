package org.akazukin.event.hook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.akazukin.event.method.IEventMethod;
import org.akazukin.event.target.IEventCondition;

@Getter
@AllArgsConstructor
@Builder(setterPrefix = "set")
public class EventHook<T> implements IEventHook<T> {
    IEventMethod<T> method;
    IEventCondition condition;
}
