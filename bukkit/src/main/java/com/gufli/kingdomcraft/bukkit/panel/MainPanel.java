package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MainPanel {

    static KingdomCraftImpl kdc;

    static String text(String key) {
        return kdc.getMessageManager().getMessage("cmdInfo" + key, true);
    }

    static String text(String key, String... placeholders) {
        return kdc.getMessageManager().getMessage("cmdInfo" + key, true, placeholders);
    }

    static String colorify(String text) {
        return kdc.getMessageManager().colorify(text);
    }

    static void withBack(InventoryBuilder builder, Runnable back) {
        if ( back != null ) {
            builder.withHotbarItem(4, ItemStackBuilder.of(Material.BARRIER)
                            .withName(ChatColor.RED + "Back")
                            .build(),
                    (player1, clickType) -> {
                        back.run();
                        return true;
                    }
            );
        }
    }

    static void open(PlatformPlayer player) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Kingdom panel");

        builder.withItem(ItemStackBuilder.of(Material.DIAMOND_SWORD)
                .withName(ChatColor.GOLD + "Kingdoms")
                .build(),
                (player1, clickType) -> {
                    KingdomListPanel.open(player, () -> open(player));
                    return true;
                }
        );

        builder.withItem(ItemStackBuilder.of(Material.SKULL_ITEM)
                .withName(ChatColor.GOLD + "Players")
                .withTexture(player.getUniqueId())
                .build(),
                (player1, clickType) -> {
                    PlayerListPanel.open(player, () -> open(player));
                    return true;
                }
        );

        player.openInventory(builder.buildS());

    }

}
