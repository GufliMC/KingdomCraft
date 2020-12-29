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
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.EventListener;
import com.gufli.kingdomcraft.api.events.PlayerAttackPlayerEvent;
import com.gufli.kingdomcraft.api.events.PlayerChatEvent;

import java.util.function.Consumer;

public class EventDispatcher {

    private final EventManagerImpl eventManager;

    public EventDispatcher(EventManagerImpl eventManager) {
        this.eventManager = eventManager;
    }

    private void execute(Consumer<EventListener> consumer) {
        eventManager.listeners.forEach(listener -> {
            try {
                consumer.accept(listener);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void dispatchLogin(PlatformPlayer player) {
        execute(l -> l.onLogin(player));
    }

    public void dispatchJoin(PlatformPlayer player) {
        execute(l -> l.onJoin(player));
    }

    public void dispatchQuit(PlatformPlayer player) {
        execute(l -> l.onQuit(player));
    }

    public void dispatchKingdomJoin(User user) {
        execute(l -> l.onKingdomJoin(user));
    }

    public void dispatchKingdomLeave(User user, Kingdom oldKingdom) {
        execute(l -> l.onKingdomLeave(user, oldKingdom));
    }

    public void dispatchKingdomCreate(Kingdom kingdom) {
        execute(l -> l.onKingdomCreate(kingdom));
    }

    public void dispatchKingdomDelete(Kingdom kingdom) {
        execute(l -> l.onKingdomDelete(kingdom));
    }

    public void dispatchPlayerAttack(PlayerAttackPlayerEvent event) {
        execute(l -> l.onPlayerAttack(event));
    }

    public void dispatchRankChange(User user, Rank oldRank) {
        execute(l -> l.onRankChange(user, oldRank));
    }

    public void dispatchReload() {
        execute(EventListener::onReload);
    }

    public void dispatchPlayerChat(PlayerChatEvent event) {
        execute(l -> l.onPlayerChat(event));
    }

}
