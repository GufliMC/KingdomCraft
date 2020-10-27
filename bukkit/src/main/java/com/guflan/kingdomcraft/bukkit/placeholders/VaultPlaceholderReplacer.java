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
