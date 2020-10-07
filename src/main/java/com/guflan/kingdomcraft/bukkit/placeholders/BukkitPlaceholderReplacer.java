package com.guflan.kingdomcraft.bukkit.placeholders;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class BukkitPlaceholderReplacer {

    public BukkitPlaceholderReplacer(KingdomCraft kdc) {
        PlaceholderManager pm = kdc.getPlaceholderManager();

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
