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

package com.guflan.kingdomcraft.bukkit.entity;

import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.bukkit.util.LocationConverter;
import com.guflan.kingdomcraft.common.entity.AbstractPlatformPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitPlayer extends AbstractPlatformPlayer {

    private final Player player;

    public BukkitPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BukkitPlayer && ((BukkitPlayer) obj).player == player;
    }

    //

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public void teleport(PlatformLocation location) {
        player.teleport(LocationConverter.convert(location));
    }

    @Override
    public PlatformLocation getLocation() {
        return LocationConverter.convert(player.getLocation());
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public void sendMessage(String msg) {
        player.sendMessage(msg);
    }
}
