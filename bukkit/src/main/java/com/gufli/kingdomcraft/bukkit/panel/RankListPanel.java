package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class RankListPanel {

    protected static void open(PlatformPlayer player, Kingdom target) {
        open(player, target, null);
    }

    protected static void open(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        for ( Rank rank : target.getRanks() ) {
            builder.withItem(ItemStackBuilder.of(Material.BOOK)
                            .withName(MainPanel.colorify(rank.getDisplay())
                                    + (rank.getLevel() > 0 ? ChatColor.GRAY + " (" + ChatColor.GOLD + rank.getLevel() + ChatColor.GRAY + ")" : ""))
                            .withLore("")
                            .withLore(ChatColor.GRAY + "Online members: "
                                    + ChatColor.GREEN + MainPanel.kdc.getOnlineUsers().stream().filter(u -> u.getRank() == rank).count()
                                    + ChatColor.GRAY + " / "
                                    + ChatColor.GRAY + rank.getMemberCount()
                            )
                            .apply((b) -> {
                                if ( rank.getMaxMembers() > 0 ) {
                                    b.withLore(ChatColor.GRAY + "Max members: " + ChatColor.GOLD + rank.getMaxMembers());
                                }
                            })
                            .build(),
                    (player1, clickType) -> {
                        if ( rank.getMemberCount() == 0 ) {
                            return false;
                        }
                        RankInfoPanel.open(player, rank, () -> open(player, target, back));
                        return true;
                    }
            );
        }

        MainPanel.withBack(builder, back);
        player.openInventory(builder.buildS());
    }

}
