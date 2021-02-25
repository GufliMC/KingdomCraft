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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventManagerImpl implements EventManager {

    private final List<EventExecutor<?>> executors = new CopyOnWriteArrayList<>();

    public <T extends Event> EventExecutor<T> addListener(Class<T> type, Consumer<T> consumer) {
        EventExecutor<T> executor = new EventExecutor<>(type, consumer);
        executors.add(executor);
        return executor;
    }

    public <T extends Event> void dispatch(T event) {
        for ( EventExecutor exe : executors ) {
            try {
                if (exe.type.isAssignableFrom(event.getClass())) {
                    exe.consumer.accept(event);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class EventExecutor<T extends Event> implements com.gufli.kingdomcraft.api.event.EventExecutor {

        private final Class<T> type;
        private final Consumer<T> consumer;

        private EventExecutor(Class<T> type, Consumer<T> consumer) {
            this.type = type;
            this.consumer = consumer;
        }

        public void unregister() {
            executors.remove(this);
        }

    }

}
