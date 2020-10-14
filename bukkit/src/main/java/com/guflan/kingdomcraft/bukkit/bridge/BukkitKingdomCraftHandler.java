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

package com.guflan.kingdomcraft.bukkit.bridge;

import com.guflan.kingdomcraft.api.domain.DomainContext;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import com.guflan.kingdomcraft.common.DefaultKingdomCraftHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitKingdomCraftHandler extends DefaultKingdomCraftHandler {

    private final MessageManager messageManager;

    private final List<Player> onlinePlayers = new ArrayList<>();

    public BukkitKingdomCraftHandler(BukkitKingdomCraftPlugin plugin, DomainContext context) {
        super(plugin, context);

        this.messageManager = new BukkitMessageManager(plugin);
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return onlinePlayers.stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public Player getPlayer(User user) {
        return getPlayer(user.getUniqueId());
    }

    @Override
    public void join(Player player) {
        super.join(player);
        onlinePlayers.add(player);
    }

    @Override
    public void quit(Player player) {
        super.quit(player);
        onlinePlayers.remove(player);
    }
}
