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

package com.guflan.kingdomcraft.bukkit.placeholders;

import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPlaceholderReplacer {

    public VaultPlaceholderReplacer(KingdomCraftBukkitPlugin plugin) {
        if ( !plugin.getServer().getPluginManager().isPluginEnabled("Vault") ) {
            return;
        }

        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider == null) {
            return;
        }

        Chat chat = chatProvider.getProvider();
        PlaceholderManager pm = plugin.getKdc().getPlaceholderManager();

        pm.addPlaceholderReplacer((player, placeholder) -> {
            Player p = Bukkit.getPlayer(player.getUniqueId());
            return plugin.getKdc().getMessageManager().colorify(chat.getPlayerPrefix(p));
        }, "prefix");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            Player p = Bukkit.getPlayer(player.getUniqueId());
            return plugin.getKdc().getMessageManager().colorify(chat.getPlayerSuffix(p));
        }, "suffix");

    }

}
