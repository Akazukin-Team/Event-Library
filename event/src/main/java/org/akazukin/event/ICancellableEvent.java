package org.akazukin.event;

public interface ICancellableEvent extends IEvent {
    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
