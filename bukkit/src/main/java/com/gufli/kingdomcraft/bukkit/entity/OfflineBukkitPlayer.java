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

package com.gufli.kingdomcraft.bukkit.entity;

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformLocation;
import com.gufli.kingdomcraft.api.gui.Inventory;
import com.gufli.kingdomcraft.common.entity.AbstractPlatformPlayer;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class OfflineBukkitPlayer extends AbstractPlatformPlayer {

    private final OfflinePlayer player;
    private final User user;

    public OfflineBukkitPlayer(OfflinePlayer player, User user) {
        this.player = player;
        this.user = user;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflineBukkitPlayer bukkitPlayer)) {
            return false;
        }

        return bukkitPlayer.player.getUniqueId().equals(player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return player.getUniqueId().hashCode();
    }

    @Override
    public User getUser() {
        return user;
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
        // nothing
    }

    @Override
    public PlatformLocation getLocation() {
        return new PlatformLocation("", 0, 0, 0);
    }

    @Override
    public boolean hasPermission(String permission) {
        return false;
    }

    @Override
    public void sendMessage(String msg) {
        // nothing
    }

    // gui

    @Override
    public void openInventory(Inventory<?, ?> inventory) {
        // nothing
    }

    @Override
    public void closeInventory() {
        // nothing
    }

}
