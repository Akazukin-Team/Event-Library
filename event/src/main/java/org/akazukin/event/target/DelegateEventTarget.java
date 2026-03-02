package org.akazukin.event.target;

public class DelegateEventTarget implements IEventCondition {
    EventTarget target;

    public DelegateEventTarget(final EventTarget target) {
        this.target = target;
    }

    @Override
    public boolean isIgnoreCondition() {
        return this.target.ignoreCondition();
    }

    @Override
    public boolean isIgnoreSuperClasses() {
        return this.target.ignoreSuperClasses();
    }

    @Override
    public int getLibraryPriority() {
        return this.target.libraryPriority();
    }

    @Override
    public int getPriority() {
        return this.target.priority();
    }
}
