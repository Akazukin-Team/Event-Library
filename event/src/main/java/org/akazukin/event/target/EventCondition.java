package org.akazukin.event.target;

import lombok.Getter;

@Getter
public class EventCondition implements IEventCondition {
    boolean ignoreCondition;
    boolean ignoreSuperClasses;
    int libraryPriority;
    int priority;

    public EventCondition(final boolean ignoreCondition, final boolean ignoreSuperClasses, final int priority) {
        this(ignoreCondition, ignoreSuperClasses, 0, priority);
    }

    public EventCondition(final boolean ignoreCondition, final boolean ignoreSuperClasses, final int libraryPriority, final int priority) {
        this.ignoreCondition = ignoreCondition;
        this.ignoreSuperClasses = ignoreSuperClasses;
        this.libraryPriority = libraryPriority;
        this.priority = priority;
    }
}
