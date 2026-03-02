package org.akazukin.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.akazukin.event.event.IEvent;
import org.akazukin.event.hook.EventHook;
import org.akazukin.event.hook.IEventHook;
import org.akazukin.event.method.IEventMethod;
import org.akazukin.event.method.ReflectionEventMethod;
import org.akazukin.event.target.DelegateEventTarget;
import org.akazukin.event.target.EventTarget;
import org.akazukin.event.target.IEventCondition;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the registration, organization, and invocation of event listeners.
 *
 * @param <T> The base type of events managed by this {@link EventManager}.
 *            All managed events must extend this type.
 *            Use {@link IEvent} or its derivatives for this purpose.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventManager<T> {
    Map<Class<? extends T>, List<IEventHook<? extends T>>> registry = new HashMap<>();

    Class<T> eventType;

    public EventManager(final Class<T> eventType) {
        this.eventType = eventType;
    }

    /**
     * Registers multiple {@link IListenable} instances to the {@link EventManager}.
     * The methods of each listener annotated with {@link EventTarget} will be processed,
     * prioritized, and added to the corresponding event's registry.
     * Only methods with a single parameter corresponding
     * to the event type will be registered.
     *
     * @param listeners an array of {@link IListenable} instances
     *                  containing methods annotated with {@link EventTarget}.
     */
    public void registerListeners(final IListenable... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }

    /**
     * Registers a {@link IListenable} instance to the {@link EventManager}.
     * The methods of the listener annotated with {@link EventTarget} will be processed,
     * prioritized, and added to the corresponding event's registry.
     * Only methods with a single parameter corresponding
     * to the event type will be registered.
     *
     * @param listener the {@link IListenable} instance
     *                 containing methods annotated with {@link EventTarget}.
     */
    @SuppressWarnings("unchecked")
    public void registerListener(final IListenable listener) {
        log.debug("Registering listener: {}", listener.getClass().getName());

        synchronized (this.registry) {
            Arrays.stream(listener.getClass().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(EventTarget.class) && m.getParameterTypes().length == 1)
                    .peek(m -> {
                        if (!m.isAccessible()) {
                            m.setAccessible(true);
                        }
                    })
                    .forEach(m -> {
                        final Class<? extends T> eventClass = (Class<? extends T>) m.getParameterTypes()[0];
                        if (!this.eventType.isAssignableFrom(eventClass)) {
                            return;
                        }

                        final EventTarget eventTarget = m.getAnnotation(EventTarget.class);

                        final IEventMethod<? extends T> method = new ReflectionEventMethod<>(eventClass, listener, m);
                        this.registerListener(method, new DelegateEventTarget(eventTarget));
                    });
        }

        log.debug("Registered listener: {}", listener.getClass().getName());
    }

    /**
     * Registers an {@link IEventMethod} and {@link IEventCondition} to the {@link EventManager}.
     * The provided method and condition will be wrapped into an {@link IEventHook},
     * prioritized, and added to the corresponding event's registry.
     * Only the specific event type defined in the method will be registered.
     *
     * @param <U>       the event type.
     * @param method    the {@link IEventMethod} instance representing the event handler
     * @param condition the {@link IEventCondition} instance containing execution requirements and priority
     */
    @SuppressWarnings("unchecked")
    public <U> void registerListener(final IEventMethod<U> method, final IEventCondition condition) {
        log.debug("Registering listener: {}, Type: {}", method.getEventClass().getClass().getName(), method.getEventType().getName());

        final IEventHook<U> hook = EventHook.<U>builder()
                .setMethod(method)
                .setCondition(condition)
                .build();
        synchronized (this.registry) {
            final List<IEventHook<? extends T>> targets = this.registry.computeIfAbsent((Class<? extends T>) method.getEventType(), k -> new CopyOnWriteArrayList<>());

            targets.add((IEventHook<? extends T>) hook);
            if (targets.size() > 1) {
                targets.sort(Comparator.comparing(h -> h.getCondition().getPriority()));
            }
        }

        log.debug("Registered listener: {}, Type: {}", method.getEventClass().getClass().getName(), method.getEventType().getName());
    }

    /**
     * Unregisters a {@link IListenable} instance from the {@link EventManager}.
     * All event hooks associated with the specified {@link IListenable} are removed.
     *
     * @param IListenable the {@link IListenable} instance to be unregistered
     *                    from the event registry.
     */
    public void unregisterListener(final IListenable IListenable) {
        log.debug("Unregistering listener: {}", IListenable.getClass().getName());
        this.registry.forEach((key, value) -> {
            value.removeIf(eventClass -> eventClass.getMethod().getEventClass() == IListenable);

            this.registry.put(key, value);
        });
        log.debug("Unregistered listener: {}", IListenable.getClass().getName());
    }

    /**
     * Invokes all registered event listeners for a specific event type and priority.
     * It filters the registered event hooks based on the event type, library priority,
     * and listener conditions before invoking the corresponding method.
     *
     * @param <U>         the event type.
     * @param clazz       the {@link Class} instance representing the event type.
     *                    This is used as a key to match the registered listeners.
     * @param event       the event instance of type {@code E} to be passed to the listener methods.
     *                    The listeners are invoked with this event as an argument.
     * @param libPriority the priority level assigned to filter which listeners
     *                    should be invoked for the given event.
     */
    @SuppressWarnings("unchecked")
    public <U extends T> void callEvent(final Class<U> clazz, final U event, final int libPriority) {
        log.debug("Processing event: {} @ {}", event.getClass().getName(), event.hashCode());

        this.registry.entrySet().stream()
                .filter(e -> e.getKey().isAssignableFrom(event.getClass()))
                .forEach(e -> e.getValue().stream()
                        .filter(h -> libPriority == h.getCondition().getLibraryPriority()
                                && (h.getMethod().getEventClass().handleEvents() || h.getCondition().isIgnoreCondition())
                                && (!h.getCondition().isIgnoreSuperClasses() || clazz.equals(e.getKey())))
                        .forEach(h -> {
                            try {
                                ((IEventHook<U>) h).getMethod().call(event);
                            } catch (final Throwable t) {
                                log.error("An error occurred while processing the EventFlag, " + event.getClass().getSimpleName() + " Priority:" + libPriority, t);
                            }
                        }));

        log.debug("Processed event: {} @ {}", event.getClass().getName(), event.hashCode());
    }
}
