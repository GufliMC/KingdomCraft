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

package com.gufli.kingdomcraft.bukkit.listeners;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Relation;
import com.gufli.kingdomcraft.api.domain.RelationType;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.event.Event;
import com.gufli.kingdomcraft.api.event.EventListener;
import com.gufli.kingdomcraft.api.event.EventResult;
import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;

public class FriendlyFireListener implements EventListener {

    private final KingdomCraftBukkitPlugin plugin;

    public FriendlyFireListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getKdc().getEventManager().addListener(this);
    }

    @Override
    @Event(ignoreAllowed = true, ignoreDenied = true)
    public EventResult onPlayerAttack(PlatformPlayer player, PlatformPlayer attacker, EventResult result) {
        if ( attacker.isAdmin() ) {
            return result;
        }

        User u1 = plugin.getKdc().getUser(player);
        User u2 = plugin.getKdc().getUser(attacker);

        if ( u1 == null || u2 == null ) {
            return result;
        }

        Kingdom k1 = u1.getKingdom();
        Kingdom k2 = u2.getKingdom();

        if ( k1 == null || k2 == null ) {
            return result;
        }

        if ( plugin.getKdc().getConfig().isFriendlyFireEnabled() ) {
            return result;
        }

        if ( k1 != k2 ) {
            Relation rel = plugin.getKdc().getRelation(k1, k2);
            RelationType type = rel == null ? RelationType.NEUTRAL : rel.getType();
            if ( !plugin.getKdc().getConfig().getFriendlyFireRelationTypes().contains(type.name()) ) {
                return result;
            }
        }

        long lastNotification = 0;
        if ( attacker.has("LAST_FRIENDLYFIRE_NOTIFICATION") ) {
            lastNotification = attacker.get("LAST_FRIENDLYFIRE_NOTIFICATION", Long.class);
        }

        if ( System.currentTimeMillis() - lastNotification > 5000 ) {
            plugin.getKdc().getMessageManager().send(attacker, "friendlyFire");
            attacker.set("LAST_FRIENDLYFIRE_NOTIFICATION", System.currentTimeMillis());
        }

        return EventResult.DENY;
    }
}
