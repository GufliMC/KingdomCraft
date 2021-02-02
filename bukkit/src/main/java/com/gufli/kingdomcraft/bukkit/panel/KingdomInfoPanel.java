package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class KingdomInfoPanel {

    protected static void open(PlatformPlayer player, Kingdom target) {
        open(player, target, null);
    }

    protected static void open(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        builder.withItem(ItemStackBuilder.of(Material.DIAMOND_SWORD)
                .withName(MainPanel.colorify(target.getDisplay()))
                .withLore("")
                .withLore(ChatColor.GRAY + "Online members: "
                        + ChatColor.GREEN + MainPanel.kdc.getOnlineUsers().stream().filter(u -> u.getKingdom() == target).count()
                        + ChatColor.GRAY + " / "
                        + ChatColor.GRAY + target.getMemberCount()
                )
                .apply((b) -> {
                    if ( target.getMaxMembers() > 0 ) {
                        b.withLore(ChatColor.GRAY + "Max members: " + ChatColor.GOLD + target.getMaxMembers());
                    }
                })
                .withLore(ChatColor.GRAY + "Created at: " + ChatColor.GOLD + MainPanel.kdc.getConfig().getDateFormat().format(target.getCreatedAt()))
                .build()
        );

        if ( !target.getRanks().isEmpty() ) {
            builder.withItem(ItemStackBuilder.of(Material.BOOK)
                            .withName(ChatColor.GOLD + "Ranks" + ChatColor.GRAY + " (" + ChatColor.GREEN + target.getRanks().size() + ChatColor.GRAY + ")")
                            .build(),
                    (player1, clickType) -> {
                        RankListPanel.open(player, target, () -> open(player, target, back));
                        return true;
                    }
            );
        }

        MainPanel.withBack(builder, back);
        player.openInventory(builder.buildS());
    }

}
