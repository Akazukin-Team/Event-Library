package org.akazukin.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventTarget {

    boolean ignoreCondition() default false;

    boolean ignoreSuperClasses() default true;

    int libraryPriority() default 0;

    int priority() default 0;
}
