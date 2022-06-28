package com.gufli.kingdomcraft.api.event;

import com.gufli.kingdomcraft.api.events.Event;

import java.util.function.Function;
import java.util.function.Supplier;

public interface EventSubscription<T extends Event> {

    void unregister();

    EventSubscription<T> unregisterIf(Supplier<Boolean> test);

    EventSubscription<T> unregisterIf(Function<T, Boolean> test);

}
