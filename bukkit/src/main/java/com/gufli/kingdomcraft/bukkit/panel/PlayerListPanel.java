package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.gui.Inventory;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class PlayerListPanel {

    protected static void open(PlatformPlayer player) {
        open(player,  null);
    }

    protected static void open(PlatformPlayer player, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Players");

        for ( User user : MainPanel.kdc.getOnlineUsers() ) {
            builder.withItem(ItemStackBuilder.of(Material.SKULL_ITEM)
                            .withName(ChatColor.GREEN + user.getName())
                            .withTexture(user.getUniqueId())
                            .build(),
                    (player1, clickType) -> {
                        Inventory<?, ?> inv = player.getInventory();
                        PlayerInfoPanel.open(player, user, () -> player.openInventory(inv));
                        return true;
                    }
            );
        }

        MainPanel.withBack(builder, back);
        player.openInventory(builder.buildS());
    }

}
