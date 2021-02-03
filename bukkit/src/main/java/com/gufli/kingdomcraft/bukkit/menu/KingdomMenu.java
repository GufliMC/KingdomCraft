package com.gufli.kingdomcraft.bukkit.menu;

import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.gui.InventoryClickType;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.gui.BukkitInventory;
import com.gufli.kingdomcraft.bukkit.gui.InventoryBuilder;
import com.gufli.kingdomcraft.bukkit.gui.ItemStackBuilder;
import com.gufli.kingdomcraft.bukkit.item.BukkitItem;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.util.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KingdomMenu {

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
                    (p, ct) -> {
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

        if ( player.getUser().getKingdom() != null ) {
            builder.withItem(getKingdomItem(player.getUser().getKingdom()),
                    (p, ct) -> {
                        openKingdomInfo(player, player.getUser().getKingdom(), () -> open(player));
                    }
            );
        }

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
                            .withSkullOwner(Bukkit.getPlayer(user.getUniqueId()))
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
                .apply(b -> {
                    Player p = Bukkit.getPlayer(target.getUniqueId());
                    if ( p != null ) {
                        b.withSkullOwner(p);
                    }
                })
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
                            openConfirmMenu(player, ChatColor.DARK_GRAY + "Kick " + user.getName(), () -> {
                                ((BukkitPlayer) player).getPlayer().chat("/k kick " + user.getName());
                                openPlayerInfo(player, target, back);
                            }, () -> {
                                openPlayerInfo(player, target, back);
                            });
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
                            openRankSelection(player, target, () -> {
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

        if ( player.hasPermission("kingdom.edit.item.other") || (player.hasPermission("kingdom.edit.item") && player.getUser().getKingdom() == target) ){
            builder.withItem(getKingdomItem(target), (p, ct) -> {
                openKingdomSelectItem(player, target, () -> openKingdomInfo(player, target, back));
            });
        } else {
            builder.withItem(getKingdomItem(target));
        }


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

        builder.withItem(ItemStackBuilder.skull()
                .withName(ChatColor.GOLD + "Members" + ChatColor.GRAY + " (" + ChatColor.GREEN + target.getMemberCount() + ChatColor.GRAY + ")")
                .build(),
                (p, ct) -> {
                    if ( target.getMemberCount() == 0 ) {
                        return false;
                    }
                    openKingdomPlayerList(player, target, () -> openKingdomInfo(player, target, back));
                    return true;
                }
        );

        if ( player.getUser().getKingdom() == target || player.hasPermission("kingdom.spawn.other") ) {
            builder.withItem(ItemStackBuilder.of("RED_BED", "BED")
                            .withName(ChatColor.GREEN + "Kingdom spawn")
                            .apply(b -> {
                                if (target.getSpawn() != null) {
                                    b.withLore("", ChatColor.GRAY + "X: " + (int) target.getSpawn().getX() + ", Y: " + (int) target.getSpawn().getY() + ", Z: " + (int) target.getSpawn().getZ());

                                    if (player.hasPermission("kingdom.setspawn.other") || (player.hasPermission("kingdom.setspawn") && player.getUser().getKingdom() == target)) {
                                        b.withLore("", ChatColor.GRAY + "Left-Click to teleport", ChatColor.GRAY + "Right-Click to change spawn");
                                    } else {
                                        b.withLore("", ChatColor.GRAY + "Left-Click to teleport");
                                    }
                                } else {
                                    b.withLore("", ChatColor.GRAY + "Spawn not set");
                                }
                            }).build(),
                    (p, ct) -> {
                        if ((player.hasPermission("kingdom.setspawn.other") || (player.hasPermission("kingdom.setspawn")
                                && player.getUser().getKingdom() == target)) && ct == InventoryClickType.RIGHT) {
                            openConfirmMenu(player, ChatColor.DARK_GRAY + "Change kingdom spawn", () -> {
                                target.setSpawn(player.getLocation());
                                kdc.saveAsync(target);
                                openKingdomInfo(player, target, back);
                            }, () -> {
                                openKingdomInfo(player, target, back);
                            });
                        } else if ( target.getSpawn() != null && (player.hasPermission("kingdom.spawn.other")
                                || (player.hasPermission("kingdom.spawn") && player.getUser().getKingdom() == target)) ) {
                            Teleporter.teleport(player, target.getSpawn());
                        }
                    }
            );
        }

        withBack(builder, back);
        player.openInventory(builder.buildS());
    }

    // Kingdom change item

    private static String upperCaseWords(String str) {
        List<String> words = new ArrayList<>();
        for ( String word : str.split(Pattern.quote(" ")) ) {
            StringBuilder sb = new StringBuilder();
            sb.append(word.substring(0, 1).toUpperCase());
            if ( word.length() > 1 ) {
                sb.append(word.substring(1).toLowerCase());
            }
            words.add(sb.toString());
        }
        return String.join(" ", words);
    }

    static void openKingdomSelectItem(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Change item");

        Map<ItemStack, String> items = new LinkedHashMap<>();

        // WOOL
        for ( ItemStackBuilder.ItemColor color: ItemStackBuilder.ItemColor.values() ) {
            items.put(ItemStackBuilder.wool(color).build(), upperCaseWords(color.name().replace("_", " ")) + " Wool");
        }

        // TERRACOTTA
        for ( ItemStackBuilder.ItemColor color: ItemStackBuilder.ItemColor.values() ) {
            items.put(ItemStackBuilder.terracotta(color).build(), upperCaseWords(color.name().replace("_", " ")) + " Terracotta");
        }

        // STAINED GLASS
        for ( ItemStackBuilder.ItemColor color: ItemStackBuilder.ItemColor.values() ) {
            items.put(ItemStackBuilder.glass(color).build(), upperCaseWords(color.name().replace("_", " ")) + " Glass");
        }

        items.put(ItemStackBuilder.of(Material.CHEST).build(), "Chest");
        items.put(ItemStackBuilder.of( "STONE_BRICKS", "SMOOTH_BRICK").build(), "Stone Bricks");
        items.put(ItemStackBuilder.of( "NETHER_BRICKS", "NETHER_BRICK").build(), "Nether Bricks");
        items.put(ItemStackBuilder.of( "BRICKS", "BRICK").build(), "Bricks");
        items.put(ItemStackBuilder.of(Material.ICE).build(), "Ice");
        items.put(ItemStackBuilder.of(Material.SNOW_BLOCK).build(), "Ice");
        items.put(ItemStackBuilder.of("OAK_LOG", "LOG").build(), "Oak Log");
        items.put(ItemStackBuilder.of("SPRUCE_LOG", "LOG", 1).build(), "Spruce Log");
        items.put(ItemStackBuilder.of("BIRCH_LOG", "LOG", 2).build(), "Birch Log");
        items.put(ItemStackBuilder.of("JUNGLE_LOG", "LOG", 3).build(), "Jungle Log");
        items.put(ItemStackBuilder.of("DARK_OAK_LOG", "LOG_2", 1).build(), "Dark Oak Log");
        items.put(ItemStackBuilder.of("ACACIA_LOG", "LOG_2", 2).build(), "Acacia Log");
        items.put(ItemStackBuilder.of("CARVED_PUMPKIN", "PUMPKIN").build(), "Carved Pumpkin");
        items.put(ItemStackBuilder.of(Material.GLOWSTONE).build(), "Glowstone");
        items.put(ItemStackBuilder.of(Material.TNT).build(), "TNT");
        items.put(ItemStackBuilder.of(Material.BOOKSHELF).build(), "Bookshelf");
        items.put(ItemStackBuilder.of(Material.EMERALD_BLOCK).build(), "Emerald Block");
        items.put(ItemStackBuilder.of(Material.DIAMOND_BLOCK).build(), "Diamond Block");
        items.put(ItemStackBuilder.of(Material.IRON_BLOCK).build(), "Iron Block");
        items.put(ItemStackBuilder.of(Material.GOLD_BLOCK).build(), "Gold Block");
        items.put(ItemStackBuilder.of(Material.OBSIDIAN).build(), "Obsidian");
        items.put(ItemStackBuilder.of(Material.FURNACE).build(), "Furnace");
        items.put(ItemStackBuilder.of("CRAFTING_TABLE", "WORKBENCH").build(), "Crafting Table");
        items.put(ItemStackBuilder.of(Material.JUKEBOX).build(), "Jukebox");


        for ( ItemStack item : items.keySet() ) {
            builder.withItem(ItemStackBuilder.of(item.clone())
                    .withName(ChatColor.YELLOW + items.get(item))
                    .build(), (p, ct) -> {
                target.setItem(new BukkitItem(item));
                kdc.saveAsync(target);
                back.run();
            });
        }

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Kingdom player list

    static void openKingdomPlayerList(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Members of " + target.getName());

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
                                .apply(isOnline, (b) -> b.withSkullOwner(Bukkit.getPlayer(uuid)))
                                .build(),
                        (p, ct) -> {
                            kdc.getUser(uuid).thenAccept((u) -> {
                                kdc.getPlugin().getScheduler().sync().execute(() -> {
                                    openPlayerInfo(player, u, () -> openKingdomPlayerList(player, target, back));
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

    // Rank list

    static void openRankList(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Ranks of " + target.getName());

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
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Members of rank " + target.getName());

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
                                .apply(isOnline, (b) -> b.withSkullOwner(Bukkit.getOfflinePlayer(uuid)))
                                .build(),
                        (p, ct) -> {
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

    static void openRankSelection(PlatformPlayer player, User target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Change rank of " + target.getName());

        for ( Rank rank : target.getKingdom().getRanks() ) {
            if ( rank == target.getRank() || (!player.hasPermission("kingdom.setrank.other") && player.getUser().getKingdom() == target.getKingdom()
                    && rank.getLevel() >= player.getUser().getRank().getLevel()) ) {
                continue;
            }

            builder.withItem(getRankItem(rank), (p, ct) -> {
                openConfirmMenu(player, ChatColor.DARK_GRAY + "Change rank of " + target.getName() + " to " + rank.getName(), () -> {
                    ((BukkitPlayer) player).getPlayer().chat("/k setrank " + target.getName() + " " + rank.getName());
                    openPlayerInfo(player, target, back);
                }, () -> openRankSelection(player, target, back));
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
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Change relation with " + kingdom.getName());

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

    // CONFIRM

    static void openConfirmMenu(PlatformPlayer player, String title, Runnable confirm, Runnable cancel) {
        BukkitInventory inv = new BukkitInventory(27, title);

        inv.setItem(11, ItemStackBuilder.of(Material.EMERALD_BLOCK)
                .withName(ChatColor.GREEN + "Confirm")
                .build(),
                (p, ct) -> {
                    confirm.run();
                    return true;
                }
        );

        inv.setItem(15, ItemStackBuilder.of(Material.REDSTONE_BLOCK)
                .withName(ChatColor.RED + "Cancel")
                .build(),
                (p, ct) -> {
                    cancel.run();
                    return true;
                }
        );

        player.openInventory(inv);
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
        ItemStackBuilder builder = null;
        if ( kingdom.getItem() != null && kingdom.getItem().getHandle() != null) {
            builder = ItemStackBuilder.of((ItemStack) kingdom.getItem().getHandle());
        } else {
            builder = ItemStackBuilder.of("WHITE_BANNER", "BANNER");
        }

        return builder.withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
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
