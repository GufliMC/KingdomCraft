package com.gufli.kingdomcraft.api.event;

import com.gufli.kingdomcraft.api.events.Event;

import java.util.function.Function;
import java.util.function.Supplier;

public interface EventExecutor<T extends Event> {

    void unregister();

    EventExecutor<T> unregisterIf(Supplier<Boolean> test);

    EventExecutor<T> unregisterIf(Function<T, Boolean> test);

}
