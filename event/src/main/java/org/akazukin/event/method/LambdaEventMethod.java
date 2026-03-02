package org.akazukin.event.method;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.akazukin.event.IListenable;

import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LambdaEventMethod<T> implements IEventMethod<T> {
    @Getter
    Class<T> eventType;
    @Getter
    IListenable eventClass;
    Consumer<T> method;

    public LambdaEventMethod(final Class<T> eventType, final IListenable eventClass, final Consumer<T> method) {
        this.eventType = eventType;
        this.eventClass = eventClass;
        this.method = method;
    }

    @SneakyThrows
    @Override
    public void call(final T event) {
        this.method.accept(event);
    }
}
