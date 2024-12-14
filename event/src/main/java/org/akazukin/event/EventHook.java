package org.akazukin.event;

import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventHook {
    Listenable eventClass;
    Method method;
    int priority;
    int libraryPriority;
    boolean ignoreCondition;
    boolean ignoreSuperClasses;
}
