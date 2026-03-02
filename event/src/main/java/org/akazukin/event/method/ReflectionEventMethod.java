package org.akazukin.event.method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.akazukin.event.IListenable;

import java.lang.reflect.Method;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReflectionEventMethod<T> implements IEventMethod<T> {
    @Getter
    Class<T> eventType;
    @Getter
    IListenable eventClass;
    Method method;

    public ReflectionEventMethod(final Class<T> eventType, final IListenable eventClass, final Method method) {
        this.eventType = eventType;
        this.eventClass = eventClass;
        this.method = method;
    }

    @SneakyThrows
    @Override
    public void call(final T event) {
        this.method.invoke(this.eventClass, event);
    }
}
