package org.akazukin.event.target;

public interface IEventCondition {
    boolean isIgnoreCondition();

    boolean isIgnoreSuperClasses();

    int getLibraryPriority();

    int getPriority();
}
