package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.gui.Inventory;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class KingdomListPanel {

    protected static void open(PlatformPlayer player) {
        open(player,  null);
    }

    protected static void open(PlatformPlayer player, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Kingdoms");

        for ( Kingdom kingdom : MainPanel.kdc.getKingdoms() ) {
            builder.withItem(ItemStackBuilder.of(Material.DIAMOND_SWORD)
                    .withName(MainPanel.colorify(kingdom.getDisplay()))
                    .withLore("")
                    .withLore(ChatColor.GRAY + "Online members: "
                            + ChatColor.GREEN + MainPanel.kdc.getOnlineUsers().stream().filter(u -> u.getKingdom() == kingdom).count()
                            + ChatColor.GRAY + " / "
                            + ChatColor.GRAY + kingdom.getMemberCount()
                    )
                    .apply((b) -> {
                        if (kingdom.getMaxMembers() > 0) {
                            b.withLore(ChatColor.GRAY + "Max members: " + ChatColor.GOLD + kingdom.getMaxMembers());
                        }
                    })
                    .withLore(ChatColor.GRAY + "Created at: " + ChatColor.GOLD + MainPanel.kdc.getConfig().getDateFormat().format(kingdom.getCreatedAt()))
                    .build(),
                    (player1, clickType) -> {
                        Inventory<?, ?> inv = player.getInventory();
                        KingdomInfoPanel.open(player, kingdom, () -> player.openInventory(inv));
                        return true;
                    }
            );
        }

        MainPanel.withBack(builder, back);
        player.openInventory(builder.buildS());
    }

}
