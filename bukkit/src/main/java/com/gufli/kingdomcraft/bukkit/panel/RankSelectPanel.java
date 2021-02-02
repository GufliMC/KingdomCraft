package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.function.Consumer;

public class RankSelectPanel {

    static void open(PlatformPlayer player, User target, Consumer<Rank> callback) {
        open(player, target, callback, null);
    }

    static void open(PlatformPlayer player, User target, Consumer<Rank> callback, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Select rank");

        for ( Rank rank : target.getKingdom().getRanks() ) {
            if ( rank == target.getRank() ) {
                continue;
            }

            builder.withItem(ItemStackBuilder.of(Material.BOOK)
                    .withName(MainPanel.colorify(rank.getDisplay()))
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
                        callback.accept(rank);
                        return true;
                    }
            );
        }

        MainPanel.withBack(builder, back);
        player.openInventory(builder.build());
    }

}
