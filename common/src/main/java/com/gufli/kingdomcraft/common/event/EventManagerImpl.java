/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.event;

import com.gufli.kingdomcraft.api.event.EventManager;
import com.gufli.kingdomcraft.api.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EventManagerImpl implements EventManager {

    private final List<EventSubscription<?>> executors = new CopyOnWriteArrayList<>();

    public <T extends Event> EventSubscription<T> addListener(Class<T> type, Consumer<T> consumer) {
        EventSubscription<T> executor = new EventSubscription<>(type, consumer);
        executors.add(executor);
        return executor;
    }

    public <T extends Event> EventSubscription<T> addListener(Class<T> type, BiConsumer<com.gufli.kingdomcraft.api.event.EventSubscription<T>, T> consumer) {
        EventSubscription<T> executor = new EventSubscription<>(type, consumer);
        executors.add(executor);
        return executor;
    }

    public <T extends Event> void dispatch(T event) {
        for ( EventSubscription exe : new ArrayList<>(executors) ) {
            if ( !exe.type.isAssignableFrom(event.getClass())) {
                continue;
            }

            try {
                exe.consumer.accept(exe, event);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                if ( executors.contains(exe) && exe.shouldUnregister(event) ) {
                    executors.remove(exe);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class EventSubscription<T extends Event> implements com.gufli.kingdomcraft.api.event.EventSubscription<T> {

        private final Class<T> type;
        private final BiConsumer<com.gufli.kingdomcraft.api.event.EventSubscription<T>, T> consumer;

        private Function<T, Boolean> unregisterTest;

        private EventSubscription(Class<T> type, BiConsumer<com.gufli.kingdomcraft.api.event.EventSubscription<T>, T> consumer) {
            this.type = type;
            this.consumer = consumer;
        }

        private EventSubscription(Class<T> type, Consumer<T> consumer) {
            this(type, (ignored, event) -> consumer.accept(event));
        }

        @Override
        public void unregister() {
            executors.remove(this);
        }

        @Override
        public EventSubscription<T> unregisterIf(Supplier<Boolean> test) {
            unregisterIf((ignored) -> test.get());
            return this;
        }

        @Override
        public EventSubscription<T> unregisterIf(Function<T, Boolean> test) {
            this.unregisterTest = test;
            return this;
        }

        private boolean shouldUnregister(T event) {
            if ( unregisterTest != null ) {
                return unregisterTest.apply(event);
            }
            return false;
        }
    }

}
