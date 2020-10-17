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

package com.guflan.kingdomcraft.bukkit;

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.bukkit.messages.BukkitMessageManager;
import com.guflan.kingdomcraft.common.AbstractKingdomCraft;
import com.guflan.kingdomcraft.common.config.KingdomCraftConfig;
import com.guflan.kingdomcraft.common.ebean.EBeanContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KingdomCraftBukkit extends AbstractKingdomCraft {

    private final MessageManager messageManager;

    private final List<PlatformPlayer> onlinePlayers = new ArrayList<>();

    public KingdomCraftBukkit(KingdomCraftBukkitPlugin plugin, KingdomCraftConfig config, EBeanContext context) {
        super(plugin, config, context);
        this.messageManager = new BukkitMessageManager(plugin);
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public List<PlatformPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public PlatformPlayer getPlayer(UUID uuid) {
        return onlinePlayers.stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public PlatformPlayer getPlayer(User user) {
        return getPlayer(user.getUniqueId());
    }

    @Override
    public void join(PlatformPlayer player) {
        super.join(player);
        onlinePlayers.add(player);
    }

    @Override
    public void quit(PlatformPlayer player) {
        super.quit(player);
        onlinePlayers.remove(player);
    }
}
