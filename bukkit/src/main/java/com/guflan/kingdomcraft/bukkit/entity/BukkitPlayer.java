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

import com.guflan.kingdomcraft.api.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitPlayer extends BukkitCommandSender implements Player {

    private final Map<String, Object> cache = new HashMap<>();
    private final org.bukkit.entity.Player player;

    public BukkitPlayer(org.bukkit.entity.Player player) {
        super(player);
        this.player = player;
    }

    public org.bukkit.entity.Player getPlayer() {
        return player;
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
    public boolean equals(Object obj) {
        return obj instanceof BukkitPlayer && ((BukkitPlayer) obj).player == player;
    }

    // cache

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public boolean has(String key) {
        return cache.containsKey(key);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) cache.get(key);
    }

}
