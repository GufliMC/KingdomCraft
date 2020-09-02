package com.igufguf.kingdomcraft.bukkit.placeholders;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class BukkitPlaceholderReplacer {

    public BukkitPlaceholderReplacer(KingdomCraftPlugin plugin) {
        PlaceholderManager pm = plugin.getPlaceholderManager();

        pm.addPlaceholderReplacer((player, placeholder) -> translate(player.getKingdom().getDisplay()),
                "kingdom");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom().getName(),
                "kingdom_name");

        pm.addPlaceholderReplacer((player, placeholder) -> translate(player.getRank().getDisplay()),
                "rank");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank().getName(),
                "rank_name");

        pm.addPlaceholderReplacer((player, placeholder) -> translate(player.getKingdom().getPrefix()),
                "kingdom_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> translate(player.getKingdom().getSuffix()),
                "kingdom_suffix");

        pm.addPlaceholderReplacer((player, placeholder) -> translate(player.getRank().getPrefix()),
                "rank_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> translate(player.getRank().getSuffix()),
                "rank_suffix");

        pm.addPlaceholderReplacer((player, placeholder) -> Bukkit.getPlayer(player.getUniqueId()).getLocation().getWorld().toString(),
                "world");

        pm.addPlaceholderReplacer((player, placeholder) -> {
                    ItemStack stack = Bukkit.getPlayer(player.getUniqueId()).getItemInHand();
                    if ( stack.getItemMeta() == null ) {
                        return "";
                    }
                    return stack.getItemMeta().getDisplayName();
                },
                "item", "weapone");
    }

    private String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
