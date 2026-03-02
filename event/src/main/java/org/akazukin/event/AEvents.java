package org.akazukin.event;

public abstract class AEvents<E> {
    private final EventManager<E> eventManager;

    public AEvents(final EventManager<E> eventManager) {
        this.eventManager = eventManager;
    }

    protected <E2 extends E> void callEvent(final Class<E2> clazz, final E2 event) {
        this.callEvent(clazz, event, 0);
    }

    protected <E2 extends E> void callEvent(final Class<E2> clazz, final E2 event, final int priority) {
        this.eventManager.callEvent(clazz, event, priority);
    }
}
