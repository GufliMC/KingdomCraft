package com.igufguf.kingdomcraft.bukkit.placeholders;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderReplacer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class BukkitPlaceholderReplacer implements PlaceholderReplacer {

    public BukkitPlaceholderReplacer(KingdomCraftPlugin plugin) {
        plugin.getPlaceholderManager().addPlaceholderReplacer(this,
                "kingdom", "kingdom_name", "rank", "rank_name", "kingdom_prefix", "kingdom_suffix",
                "rank_prefix", "rank_suffix", "world", "item", "weapon");
    }

    @Override
    public String replace(Player player, String placeholder) {

        if ( placeholder.equals("kingdom") ) {
            return translate(player.getKingdom().getDisplay());
        }

        if ( placeholder.equals("kingdom_name") ) {
            return translate(player.getKingdom().getName());
        }

        if ( placeholder.equals("rank") ) {
            return translate(player.getRank().getDisplay());
        }

        if ( placeholder.equals("rank_name") ) {
            return translate(player.getRank().getName());
        }

        if ( placeholder.equals("kingdom_prefix") ) {
            return translate(player.getKingdom().getPrefix());
        }

        if ( placeholder.equals("kingdom_suffix") ) {
            return translate(player.getKingdom().getSuffix());
        }

        if ( placeholder.equals("rank_prefix") ) {
            return translate(player.getRank().getPrefix());
        }

        if ( placeholder.equals("rank_suffix") ) {
            return translate(player.getRank().getSuffix());
        }

        org.bukkit.entity.Player bplayer = Bukkit.getPlayer(player.getUniqueId());

        if ( placeholder.equals("world") ) {
            return bplayer.getLocation().getWorld().toString();
        }

        if ( placeholder.equals("item") || placeholder.equals("weapon")) {
            ItemStack stack = bplayer.getItemInHand();
            if ( stack.getItemMeta() == null ) {
                return "";
            }
            return stack.getItemMeta().getDisplayName();
        }

        return null;
    }

    private String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
