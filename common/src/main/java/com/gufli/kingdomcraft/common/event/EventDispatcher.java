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

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.Event;
import com.gufli.kingdomcraft.api.event.EventListener;
import com.gufli.kingdomcraft.api.event.EventPriority;
import com.gufli.kingdomcraft.api.event.EventResult;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class EventDispatcher {

    private final EventManagerImpl eventManager;

    public EventDispatcher(EventManagerImpl eventManager) {
        this.eventManager = eventManager;
    }

    public EventFunction<Void> dispatch(String name, Class<?>... classes) {
        return executor(name, classes);
    }

    public EventFunction<EventResult> dispatchResult(String name, Class<?>... classes) {
        return dispatchResult(name, EventResult.NEUTRAL, classes);
    }

    public EventFunction<EventResult> dispatchResult(String name, EventResult initial, Class<?>... classes) {
        return supplier(name, initial, classes);
    }

    // EVENTS

    public void dispatchLogin(PlatformPlayer player) {
        dispatch("onLogin", PlatformPlayer.class).execute(player);
    }

    public void dispatchJoin(PlatformPlayer player) {
        dispatch("onJoin", PlatformPlayer.class).execute(player);
    }

    public void dispatchQuit(PlatformPlayer player) {
        dispatch("onQuit", PlatformPlayer.class).execute(player);
    }

    public void dispatchKingdomJoin(PlatformPlayer player) {
        dispatch("onKingdomJoin", PlatformPlayer.class).execute(player);
    }

    public void dispatchKingdomLeave(PlatformPlayer player, Kingdom previousKingdom) {
        dispatch("onKingdomLeave", PlatformPlayer.class, Kingdom.class).execute(player, previousKingdom);
    }

    public void dispatchKingdomCreate(Kingdom kingdom) {
        dispatch("onKingdomCreate", Kingdom.class).execute(kingdom);
    }

    public void dispatchKingdomDelete(Kingdom kingdom) {
        dispatch("onKingdomDelete", Kingdom.class).execute(kingdom);
    }

    public EventResult dispatchPlayerAttack(PlatformPlayer player, PlatformPlayer attacker) {
        return dispatchResult("onPlayerAttack", PlatformPlayer.class, PlatformPlayer.class).execute(player, attacker);
    }

    public void dispatchRankChange(PlatformPlayer player, Rank previousRank) {
        dispatch("onRankChange", PlatformPlayer.class, Rank.class).execute(previousRank);
    }

    public void dispatchReload() {
        dispatch("onReload").execute();
    }

    // HANDLING

    private EventFunction<Void> executor(String name, Class<?>... classes) {
        Map<Method, EventListener> listeners = mapListeners(name, classes);
        List<Method> methods = sortMethods(listeners);

        return (objects) -> {
            for ( Method method : methods ) {
                EventListener listener = listeners.get(method);
                try {
                    method.invoke(listener, objects);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        };
    }

    private EventFunction<EventResult> supplier(String name, EventResult initial, Class<?>... classes) {
        classes = Arrays.copyOf(classes, classes.length + 1);
        classes[classes.length - 1] = EventResult.class;

        Map<Method, EventListener> listeners = mapListeners(name, classes);
        List<Method> methods = sortMethods(listeners);

        return (objects) -> {
            EventResult result = initial;

            for ( Method method : methods ) {

                if ( method.isAnnotationPresent(Event.class) ) {
                    Event event = method.getAnnotation(Event.class);
                    if ( result == EventResult.ALLOW && event.ignoreAllowed() ) {
                        continue;
                    }
                    if ( result == EventResult.DENY && event.ignoreDenied() ) {
                        continue;
                    }
                }

                EventListener listener = listeners.get(method);
                try {
                    Object value = method.invoke(listener, objects);
                    if ( value instanceof EventResult ) {
                        result = (EventResult) value;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return result;
        };
    }

    private Map<Method, EventListener> mapListeners(String name, Class<?>... classes) {
        Map<Method, EventListener> listeners = new HashMap<>();
        for ( EventListener listener : eventManager.listeners ) {
            try {
                Method method = listener.getClass().getMethod(name, classes);
                listeners.put(method, listener);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return listeners;
    }

    private List<Method> sortMethods(Map<Method, EventListener> listeners) {
        return listeners.keySet().stream().sorted(Comparator.comparing(method -> {
            if ( method.isAnnotationPresent(Event.class) ) {
                Event event = method.getAnnotation(Event.class);
                return event.priority();
            }
            return EventPriority.NORMAL;
        })).collect(Collectors.toList());
    }

    private interface EventFunction<T> {
        T execute(Object... objects);
    }

}
