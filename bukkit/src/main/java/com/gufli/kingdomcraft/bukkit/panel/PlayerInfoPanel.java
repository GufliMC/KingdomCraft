package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.text.DateFormat;

public class PlayerInfoPanel {

    protected static void open(PlatformPlayer player, User target) {
        open(player, target, null);
    }

    protected static void open(PlatformPlayer player, User target, Runnable back) {
        User user = player.getUser();

        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        DateFormat df = MainPanel.kdc.getConfig().getDateFormat();
        builder.withItem(ItemStackBuilder.of(Material.SKULL_ITEM)
                .withName(ChatColor.GOLD + target.getName())
                .apply(b -> {
                    if ( MainPanel.kdc.getPlayer(target) != null ) {
                        b.withLore(ChatColor.GRAY + "Last seen: " + ChatColor.GOLD + "now");
                    } else {
                        b.withLore(ChatColor.GRAY + "Last seen: " + ChatColor.GOLD + df.format(target.getUpdatedAt()));
                    }
                })
                .withLore(ChatColor.GRAY + "First login: " + ChatColor.GOLD + df.format(target.getCreatedAt()))
                .setPlayer(Bukkit.getOfflinePlayer(target.getUniqueId()))
                .build()
        );

        if ( target.getKingdom() != null ) {
            builder.withItem(ItemStackBuilder.of(Material.DIAMOND_SWORD)
                            .withName(MainPanel.kdc.getMessageManager().colorify(target.getKingdom().getDisplay()))
                            .build(),
                    (player1, clickType) -> {
                        KingdomInfoPanel.open(player, target.getKingdom(), () -> open(player, target, back));
                        return true;
                    }
            );

            if ( target.getRank() != null ) {
                builder.withItem(ItemStackBuilder.of(Material.BOOK)
                            .withName(MainPanel.kdc.getMessageManager().colorify(target.getRank().getDisplay()))
                            .build(),
                    (player1, clickType) -> {
                        RankInfoPanel.open(player, target.getRank(), () -> open(player, target, back));
                        return true;
                    }
                );
            }

            if ( player.hasPermission("kingdom.kick.other")
                    || (player.hasPermission("kingdom.kick") && player.getUser().getKingdom() == target.getKingdom()
                    && user.getRank() != null && (target.getRank() == null || user.getRank().getLevel() > target.getRank().getLevel())) ) {
                builder.withItem(ItemStackBuilder.of(Material.IRON_AXE)
                            .withName(ChatColor.RED + "Kick")
                            .build(),
                        (player1, clickType) -> {
                            ((BukkitPlayer) player).getPlayer().chat("/k kick " + user.getName());
                            return true;
                        }
                );
            }

            if ( !target.getKingdom().getRanks().isEmpty() && (
                    target.getKingdom().getRanks().size() != 1 || target.getRank() == null) && (
                            player.hasPermission("kingdom.setrank.other")
                                    || (player.hasPermission("kingdom.setrank") && player.getUser().getKingdom() == target.getKingdom()
                                    && user.getRank() != null && (target.getRank() == null || user.getRank().getLevel() > target.getRank().getLevel())
                            )
                    )
            ) {
                builder.withItem(ItemStackBuilder.of(Material.BLAZE_POWDER)
                                .withName(ChatColor.GREEN + "Change rank")
                                .build(),
                        (player1, clickType) -> {
                            RankSelectPanel.open(player, target,
                                    rank -> ((BukkitPlayer) player).getPlayer().chat("/k setrank " + user.getName() + " " + rank.getName()),
                                    () -> open(player, target, back));
                            return true;
                        }
                );
            }

        } else {
            if ( player.getUser().getKingdom() != null && player.getUser().getKingdom().isInviteOnly()
                    && player.hasPermission("kingdom.invite") ) {
                builder.withItem(ItemStackBuilder.of(Material.BOAT)
                                .withName(ChatColor.AQUA + "Invite")
                                .build(),
                        (player1, clickType) -> {
                            ((BukkitPlayer) player).getPlayer().chat("/k invite " + user.getName());
                            return true;
                        }
                );
            }
        }

        MainPanel.withBack(builder, back);
        player.openInventory(builder.buildS());
    }

}
