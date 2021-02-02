package com.gufli.kingdomcraft.bukkit.menu;

import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MainMenu {

    static KingdomCraftImpl kdc;

    private static String text(String key) {
        return kdc.getMessageManager().getMessage("cmdInfo" + key, true);
    }

    private static String text(String key, String... placeholders) {
        return kdc.getMessageManager().getMessage("cmdInfo" + key, true, placeholders);
    }

    private static String colorify(String text) {
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

    // Main Menu

    public static void open(PlatformPlayer player) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Kingdom panel");

        builder.withItem(ItemStackBuilder.of(Material.DIAMOND_SWORD)
                .withName(ChatColor.GOLD + "Kingdoms")
                .build(),
                (p, ct) -> {
                    openKingdomsList(player, () -> open(player));
                }
        );

        builder.withItem(ItemStackBuilder.skull()
                .withName(ChatColor.GOLD + "Players")
                .build(),
                (p, ct) -> {
                    openPlayerList(player, () -> open(player));
                }
        );

        player.openInventory(builder.buildS());
    }

    // Kingdoms list

    static void openKingdomsList(PlatformPlayer player, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Kingdoms");

        for ( Kingdom kingdom : kdc.getKingdoms() ) {
            builder.withItem(getKingdomItem(kingdom),
                    (p, ct) -> {
                        openKingdomInfo(player, kingdom, () -> openKingdomsList(player, back));
                        return true;
                    }
            );
        }

        withBack(builder, back);
        player.openInventory(builder.buildS());
    }

    // Players list

    static void openPlayerList(PlatformPlayer player, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Players");

        for ( User user : kdc.getOnlineUsers() ) {
            builder.withItem(ItemStackBuilder.skull()
                            .withName(ChatColor.GREEN + user.getName())
                            .withSkullOwner(Bukkit.getOfflinePlayer(user.getUniqueId()))
                            .build(),
                    (p, ct) -> {
                        openPlayerInfo(player, user, () -> openPlayerList(player, back));
                    }
            );
        }

        withBack(builder, back);
        player.openInventory(builder.buildS());
    }

    // Player info

    public static void openPlayerInfo(PlatformPlayer player, User target) {
        openPlayerInfo(player, target, null);
    }

    static void openPlayerInfo(PlatformPlayer player, User target, Runnable back) {
        User user = player.getUser();

        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        ZoneId timeZone = kdc.getConfig().getTimeZone();

        builder.withItem(ItemStackBuilder.skull()
                .withName(ChatColor.GOLD + target.getName())
                .apply(b -> {
                    if ( target.getKingdom() != null ) {
                        b.withLore("");
                        b.withLore(ChatColor.GRAY + "Kingdom: " + colorify(target.getKingdom().getDisplay()));

                        if ( target.getRank() != null ) {
                            b.withLore(ChatColor.GRAY + "Rank: " + colorify(target.getRank().getDisplay()));
                        }
                    }
                })
                .apply(b -> {
                    b.withLore("");
                    if ( kdc.getPlayer(target) != null ) {
                        b.withLore(ChatColor.GRAY + "Last seen: " + ChatColor.GOLD + "now");
                        return;
                    }

                    ZonedDateTime zdt = target.getUpdatedAt().atZone(timeZone);

                    if ( zdt.toLocalDate().equals(LocalDate.now(timeZone)) ) {
                        b.withLore(ChatColor.GRAY + "Last seen: " + ChatColor.GOLD
                                + zdt.format(kdc.getConfig().getTimeFormat()));
                    } else {
                        b.withLore(ChatColor.GRAY + "Last seen: " + ChatColor.GOLD
                                + zdt.format(kdc.getConfig().getDateFormat()));
                    }
                })
                .withLore(ChatColor.GRAY + "First login: " + ChatColor.GOLD + target.getCreatedAt().atZone(timeZone).format(kdc.getConfig().getDateFormat()))
                .withSkullOwner(((BukkitPlayer) player).getPlayer())
                .build()
        );

        if ( target.getKingdom() != null ) {
            builder.withItem(getKingdomItem(target.getKingdom()),
                    (p, ct) -> {
                        openKingdomInfo(player, target.getKingdom(), () -> openPlayerInfo(player, target, back));
                        return true;
                    }
            );

            if ( player.hasPermission("kingdom.kick.other") || (
                    player.hasPermission("kingdom.kick")
                            && player.getUser().getKingdom() == target.getKingdom()
                            && user.getRank() != null
                            && (target.getRank() == null || user.getRank().getLevel() > target.getRank().getLevel())
            )) {
                builder.withItem(ItemStackBuilder.of(Material.IRON_AXE)
                                .withName(ChatColor.RED + "Kick")
                                .build(),
                        (p, ct) -> {
                            ((BukkitPlayer) player).getPlayer().chat("/k kick " + user.getName());
                        }
                );
            }

            if ( !target.getKingdom().getRanks().isEmpty()
                    && (target.getKingdom().getRanks().size() != 1 || target.getRank() == null)
                    && (player.hasPermission("kingdom.setrank.other") || (
                                    player.hasPermission("kingdom.setrank")
                                            && player.getUser().getKingdom() == target.getKingdom()
                                            && user.getRank() != null
                                            && (target.getRank() == null || user.getRank().getLevel() > target.getRank().getLevel())
                            )
                    )
            ) {
                builder.withItem(ItemStackBuilder.of(Material.BLAZE_POWDER)
                                .withName(ChatColor.GREEN + "Change rank")
                                .build(),
                        (p, ct) -> {
                            openRankSelection(player, target, rank -> {
                                ((BukkitPlayer) player).getPlayer().chat("/k setrank " + user.getName() + " " + rank.getName());
                            }, () -> {
                                openPlayerInfo(player, target, back);
                            });
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
                        (p, ct) -> {
                            ((BukkitPlayer) player).getPlayer().chat("/k invite " + user.getName());
                        }
                );
            }
        }

        withBack(builder, back);
        player.openInventory(builder.buildS());
    }

    // Kingdom info

    public static void openKingdomInfo(PlatformPlayer player, Kingdom target) {
        openKingdomInfo(player, target, null);
    }

    static void openKingdomInfo(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        builder.withItem(getKingdomItem(target));

        if ( !target.getRanks().isEmpty() ) {
            builder.withItem(ItemStackBuilder.of(Material.BOOK)
                            .withName(ChatColor.GOLD + "Ranks" + ChatColor.GRAY + " (" + ChatColor.GREEN + target.getRanks().size() + ChatColor.GRAY + ")")
                            .build(), (p, cct) -> {
                        openRankList(player, target, () -> openKingdomInfo(player, target, back));
                    }
            );
        }

        builder.withItem(ItemStackBuilder.of(Material.GOLD_CHESTPLATE)
                .withName(ChatColor.GOLD + "Relations")
                .build(),
                (p, ct) -> {
                    openKingdomRelationsList(player, target, () -> openKingdomInfo(player, target, back));
                }
        );

        withBack(builder, back);
        player.openInventory(builder.buildS());
    }

    // Rank list

    static void openRankList(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        for ( Rank rank : target.getRanks() ) {
            builder.withItem(getRankItem(rank), (p, ct) -> {
                if ( rank.getMemberCount() == 0 ) {
                    return false;
                }
                openRankPlayerList(player, rank, () -> openRankList(player, target, back));
                return true;
            });
        }

        withBack(builder, back);
        player.openInventory(builder.buildS());
    }

    // Rank player list

    static void openRankPlayerList(PlatformPlayer player, Rank target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        kdc.getPlugin().getScheduler().async().execute(() -> {
            Map<UUID, String> members = target.getMembers();

            List<UUID> sorted = members.keySet().stream()
                    .sorted(Comparator.comparing(uuid -> kdc.getPlayer(uuid) != null))
                    .collect(Collectors.toList());

            for ( UUID uuid : sorted ) {
                String name = members.get(uuid);
                boolean isOnline = kdc.getPlayer(uuid) != null;

                builder.withItem(ItemStackBuilder.skull()
                                .withName(isOnline ? ChatColor.GREEN + name : ChatColor.GRAY + name)
                                .apply(isOnline, (b) -> b.withSkullOwner(((BukkitPlayer) player).getPlayer()))
                                .build(),
                        (player1, clickType) -> {
                            kdc.getUser(uuid).thenAccept((u) -> {
                                kdc.getPlugin().getScheduler().sync().execute(() -> {
                                    openPlayerInfo(player, u, () -> openRankPlayerList(player, target, back));
                                });
                            });
                            return true;
                        }
                );
            }

            withBack(builder, back);
            kdc.getPlugin().getScheduler().sync().execute(() ->
                    player.openInventory(builder.buildS()));
        });
    }

    // Rank selection

    static void openRankSelection(PlatformPlayer player, User target, Consumer<Rank> callback, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Select rank");

        for ( Rank rank : target.getKingdom().getRanks() ) {
            if ( rank == target.getRank() ) {
                continue;
            }

            builder.withItem(getRankItem(rank), (p, ct) -> {
                callback.accept(rank);
            });
        }

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Kingdom relations list

    private final static Map<RelationType, Integer> RELATION_ORDER;

    static {
        RELATION_ORDER = new HashMap<>();
        RELATION_ORDER.put(RelationType.ALLY, 1);
        RELATION_ORDER.put(RelationType.TRUCE, 2);
        RELATION_ORDER.put(RelationType.ENEMY, 3);
        RELATION_ORDER.put(RelationType.NEUTRAL, 4);
    }

    static void openKingdomRelationsList(PlatformPlayer player, Kingdom kingdom, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Relations with " + kingdom.getName());

        Map<Kingdom, RelationType> relations = kdc.getRelations(kingdom).stream()
                .collect(Collectors.toMap(rel -> rel.getOther(kingdom), Relation::getType));

        List<Kingdom> kingdoms = relations.keySet().stream()
                .sorted(Comparator.comparing(kd -> RELATION_ORDER.get(relations.get(kd))))
                .collect(Collectors.toList());

        kingdoms.addAll(kdc.getKingdoms().stream()
                .filter(kd -> kd != kingdom && !kingdoms.contains(kd))
                .collect(Collectors.toSet()));

        for ( Kingdom kd : kingdoms ) {
            RelationType rel = relations.getOrDefault(kd, RelationType.NEUTRAL);

            ItemStack item;
            if ( rel == RelationType.ALLY ) {
                item = ItemStackBuilder.of(Material.SLIME_BALL)
                        .withName(ChatColor.WHITE + colorify(kd.getDisplay()))
                        .withLore("", ChatColor.AQUA + "Ally of " + ChatColor.GOLD + colorify(kingdom.getDisplay()))
                        .build();
            } else if ( rel == RelationType.ENEMY ) {
                item = ItemStackBuilder.of(Material.FIREBALL)
                        .withName(ChatColor.WHITE + colorify(kd.getDisplay()))
                        .withLore("", ChatColor.RED + "Enemy of " + ChatColor.GOLD + colorify(kingdom.getDisplay()))
                        .build();
            } else if ( rel == RelationType.TRUCE ) {
                item = ItemStackBuilder.of(Material.MAGMA_CREAM)
                        .withName(ChatColor.WHITE + colorify(kd.getDisplay()))
                        .withLore("", ChatColor.DARK_PURPLE + "In truce with " + ChatColor.GOLD + colorify(kingdom.getDisplay()))
                        .build();
            } else {
                item = ItemStackBuilder.of(Material.FIREWORK_CHARGE)
                        .withName(ChatColor.WHITE + colorify(kd.getDisplay()))
                        .withLore("", ChatColor.GRAY + "Neutral with " + ChatColor.GOLD + colorify(kingdom.getDisplay()))
                        .build();
            }

            if ( kingdom == player.getUser().getKingdom() ) {
                builder.withItem(item, (p, ct) -> {
                    openKingdomRelationSelect(player, kd, () -> openKingdomRelationsList(player, kingdom, back));
                });
            } else {
                builder.withItem(item);
            }
        }

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Kingdom relation menu

    static void openKingdomRelationSelect(PlatformPlayer player, Kingdom kingdom, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Relations with " + kingdom.getName());

        Relation relation = kdc.getRelation(player.getUser().getKingdom(), kingdom);
        RelationType rel = relation != null ? relation.getType() : RelationType.NEUTRAL;

        Relation receivedRequest = kdc.getRelationRequest(kingdom, player.getUser().getKingdom());
        Relation sentRequest = kdc.getRelationRequest(player.getUser().getKingdom(), kingdom);

        if ( rel != RelationType.ALLY && player.hasPermission("kingdom.ally") && (sentRequest == null || sentRequest.getType() != RelationType.ALLY) ) {
            builder.withItem(ItemStackBuilder.of(Material.SLIME_BALL)
                    .withName(colorify(kingdom.getDisplay()))
                    .apply(b -> {
                        if ( receivedRequest != null && receivedRequest.getType() == RelationType.ALLY ) {
                            b.withLore(ChatColor.GRAY + "Click to " + ChatColor.GREEN + "accept" + ChatColor.GRAY + " their request.");
                        } else {
                            b.withLore(ChatColor.GRAY + "Click to request an alliance.");
                        }
                    })
                    .build(),
                    (p, ct) -> {
                        kdc.getCommandDispatcher().execute(player, new String[] {"ally", kingdom.getName()});
                    }
            );
        }
        if ( rel != RelationType.ENEMY && player.hasPermission("kingdom.enemy")) {
            builder.withItem(ItemStackBuilder.of(Material.FIREBALL)
                    .withName(colorify(kingdom.getDisplay()))
                    .apply(b -> {
                         b.withLore(ChatColor.GRAY + "Click to declare them enemies.");
                    })
                    .build(),
                    (p, ct) -> {
                        kdc.getCommandDispatcher().execute(player, new String[] {"enemy", kingdom.getName()});
                    }
            );
        }
        if ( rel == RelationType.ENEMY && player.hasPermission("kingdom.truce")
                && (sentRequest == null || sentRequest.getType() != RelationType.TRUCE)) {
            builder.withItem(ItemStackBuilder.of(Material.MAGMA_CREAM)
                    .withName(colorify(kingdom.getDisplay()))
                    .apply(b -> {
                        if ( receivedRequest != null && receivedRequest.getType() == RelationType.TRUCE ) {
                            b.withLore(ChatColor.GRAY + "Click to " + ChatColor.GREEN + "accept" + ChatColor.GRAY + " their request.");
                        } else {
                            b.withLore(ChatColor.GRAY + "Click to request a truce.");
                        }
                    })
                    .build(),
                    (p, ct) -> {
                        kdc.getCommandDispatcher().execute(player, new String[] {"truce", kingdom.getName()});
                    }
            );
        }
        if ( rel != RelationType.NEUTRAL && player.hasPermission("kingdom.neutral") ) {
            builder.withItem(ItemStackBuilder.of(Material.FIREWORK_CHARGE)
                    .withName(colorify(kingdom.getDisplay()))
                    .apply(b -> {
                        if ( rel != RelationType.ALLY ) {
                            if (receivedRequest != null && receivedRequest.getType() == RelationType.TRUCE) {
                                b.withLore(ChatColor.GRAY + "Click to " + ChatColor.GREEN + "accept" + ChatColor.GRAY + " their request.");
                            } else {
                                b.withLore(ChatColor.GRAY + "Click to request a neutral status.");
                            }
                        } else {
                            b.withLore(ChatColor.GRAY + "Click to end the alliance with them.");
                        }
                    })
                    .build(),
                    (p, ct) -> {
                        kdc.getCommandDispatcher().execute(player, new String[] {"neutral", kingdom.getName()});
                    }
            );
        }

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // ITEMS

    static ItemStack getRankItem(Rank rank) {
        return ItemStackBuilder.of(Material.BOOK)
                .withName(ChatColor.WHITE + colorify(rank.getDisplay())
                        + (rank.getLevel() > 0 ? ChatColor.GRAY + " (" + ChatColor.GOLD + rank.getLevel() + ChatColor.GRAY + ")" : ""))
                .withLore("")
                .withLore(ChatColor.GRAY + "Online members: "
                        + ChatColor.GREEN + kdc.getOnlineUsers().stream().filter(u -> u.getRank() == rank).count()
                        + ChatColor.GRAY + " / "
                        + ChatColor.GRAY + rank.getMemberCount()
                )
                .apply((b) -> {
                    if ( rank.getMaxMembers() > 0 ) {
                        b.withLore(ChatColor.GRAY + "Max members: " + ChatColor.GOLD + rank.getMaxMembers());
                    }
                })
                .build();
    }

    static ItemStack getKingdomItem(Kingdom kingdom) {
        return ItemStackBuilder.of(Material.DIAMOND_SWORD)
                .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                .withLore("")
                .withLore(ChatColor.GRAY + "Online members: "
                        + ChatColor.GREEN + kdc.getOnlineUsers().stream().filter(u -> u.getKingdom() == kingdom).count()
                        + ChatColor.GRAY + " / "
                        + ChatColor.GRAY + kingdom.getMemberCount()
                )
                .apply((b) -> {
                    if ( kingdom.getMaxMembers() > 0 ) {
                        b.withLore(ChatColor.GRAY + "Max members: " + ChatColor.GOLD + kingdom.getMaxMembers());
                    }
                })
                .withLore(ChatColor.GRAY + "Created at: " + ChatColor.GOLD +
                        kingdom.getCreatedAt()
                        .atZone(kdc.getConfig().getTimeZone())
                        .format(kdc.getConfig().getDateFormat()))
                .build();
    }



}
