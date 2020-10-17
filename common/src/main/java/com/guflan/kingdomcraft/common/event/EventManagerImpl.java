/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.event;

import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.api.event.EventManager;

import java.util.ArrayList;
import java.util.List;

public class EventManagerImpl implements EventManager {

    final List<EventListener> listeners = new ArrayList<>();

    @Override
    public void addListener(EventListener listener) {
        if ( !listeners.contains(listener) ) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

}
