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

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.event.EventListener;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;

import java.util.ArrayList;
import java.util.List;

public class DefaultEventManager implements EventManager {

    private final List<EventListener> listeners = new ArrayList<>();

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

    @Override
    public void join(Player player) {
        listeners.forEach(l -> l.onJoin(player));
    }

    @Override
    public void quit(Player player) {
        listeners.forEach(l -> l.onQuit(player));
    }

    @Override
    public void kingdomJoin(User player) {
        listeners.forEach(l -> l.onKingdomJoin(player));
    }

    @Override
    public void kingdomLeave(User player, Kingdom oldKingdom) {
        listeners.forEach(l -> l.onKingdomLeave(player, oldKingdom));
    }

    @Override
    public void kingdomCreate(Kingdom kingdom) {
        listeners.forEach(l -> l.onKingdomCreate(kingdom));
    }

    @Override
    public void kingdomDelete(Kingdom kingdom) {
        listeners.forEach(l -> l.onKingdomDelete(kingdom));
    }

    @Override
    public void playerAttack(PlayerAttackPlayerEvent event) {
        listeners.forEach(l -> l.onPlayerAttack(event));
    }
}
