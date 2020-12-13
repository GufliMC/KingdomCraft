package com.gufli.kingdomcraft.api.event;

public @interface Event {

    boolean ignoreDenied() default false;

    boolean ignoreAllowed() default false;

    EventPriority priority() default EventPriority.NORMAL;

}
