package org.akazukin.event.event;

public interface ICancellableEvent extends IEvent {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
