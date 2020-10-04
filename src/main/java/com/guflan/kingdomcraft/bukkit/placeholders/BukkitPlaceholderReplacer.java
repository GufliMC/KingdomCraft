package com.guflan.kingdomcraft.bukkit.placeholders;

import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.bukkit.KingdomCraft;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class BukkitPlaceholderReplacer {

    public BukkitPlaceholderReplacer(KingdomCraft plugin) {
        PlaceholderManager pm = plugin.getBridge().getPlaceholderManager();

        pm.addPlaceholderReplacer((player, placeholder) -> Bukkit.getPlayer(player.getUniqueId()).getLocation().getWorld().toString(),
                "world");

        pm.addPlaceholderReplacer((player, placeholder) -> {
                    ItemStack stack = Bukkit.getPlayer(player.getUniqueId()).getItemInHand();
                    if ( stack.getItemMeta() == null ) {
                        return "";
                    }
                    return stack.getItemMeta().getDisplayName();
                },
                "item", "weapon");
    }

}
