package com.guflan.kingdomcraft.bukkit.placeholders;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class BukkitPlaceholderReplacer {

    public BukkitPlaceholderReplacer(KingdomCraftPlugin plugin) {
        PlaceholderManager pm = plugin.getPlaceholderManager();

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
