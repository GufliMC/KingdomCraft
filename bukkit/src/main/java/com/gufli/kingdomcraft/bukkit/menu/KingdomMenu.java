/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit.menu;

import com.gufli.kingdomcraft.api.domain.*;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.gui.InventoryClickType;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.gui.*;
import com.gufli.kingdomcraft.bukkit.item.BukkitItem;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KingdomMenu {

    static KingdomCraftImpl kdc;

    private static String text(String key) {
        String msg = kdc.getMessages().getMessage(key, true);
        if (msg == null) return "";
        return msg;
    }

    private static String text(String key, String... placeholders) {
        String msg = kdc.getMessages().getMessage(key, true, placeholders);
        if (msg == null) return "";
        return msg;
    }

    private static String colorify(String text) {
        return kdc.getMessages().colorify(text);
    }

    static void withBack(InventoryBuilder builder, Runnable back) {
        if (back != null) {
            builder.withHotbarItem(4, ItemStackBuilder.of(Material.BARRIER)
                            .withName(text("menuBack"))
                            .build(),
                    (p, ct) -> {
                        back.run();
                        return true;
                    }
            );
        }
    }

    static void withBack(PaginationBuilder builder, Runnable back) {
        if (back != null) {
            builder.withHotbarItem(4, ItemStackBuilder.of(Material.BARRIER)
                            .withName(text("menuBack"))
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
        InventoryBuilder builder = InventoryBuilder.create().withTitle(text("menuTitle"));

        builder.withItem(ItemStackBuilder.of(Material.DIAMOND_SWORD)
                        .withName(text("menuItemKingdomsList", kdc.getKingdoms().size() + ""))
                        .build(),
                (p, ct) -> {
                    openKingdomsList(player, () -> open(player));
                }
        );

        if (player.getUser().getKingdom() != null) {
            builder.withItem(getKingdomItem(player.getUser().getKingdom()),
                    (p, ct) -> {
                        openKingdomInfo(player, player.getUser().getKingdom(), () -> open(player));
                    }
            );
        }

        builder.withItem(ItemStackBuilder.skull()
                        .withName(text("menuItemPlayersList", kdc.getOnlinePlayers().size() + ""))
                        .build(),
                (p, ct) -> {
                    openPlayerList(player, () -> open(player));
                }
        );

        player.openInventory(builder.build());
    }

    // Kingdoms list

    static void openKingdomsList(PlatformPlayer player, Runnable back) {
        PaginationBuilder builder = PaginationBuilder.create().withTitle(text("menuPlayersListTitle"));

        List<Kingdom> kingdoms = new ArrayList<>(kdc.getKingdoms());
        builder.withItems(kingdoms.size(), index -> {
            Kingdom kingdom = kingdoms.get(index);
            return new BukkitInventoryItem(getKingdomItem(kingdom),
                    (p, ct) -> {
                        openKingdomInfo(player, kingdom, () -> openKingdomsList(player, back));
                        return true;
                    });
        });

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Players list

    static void openPlayerList(PlatformPlayer player, Runnable back) {
        PaginationBuilder builder = PaginationBuilder.create().withTitle(text("menuPlayersListTitle"));

        List<User> users = kdc.getOnlinePlayers().stream()
                .filter(op -> ((BukkitPlayer) player).getPlayer().canSee(((BukkitPlayer) op).getPlayer()))
                .map(PlatformPlayer::getUser)
                .collect(Collectors.toList());

        builder.withItems(users.size(), index -> {
            User user = users.get(index);
            return new BukkitInventoryItem(ItemStackBuilder.skull()
                    .withName(ChatColor.GREEN + user.getName())
                    .withSkullOwner(Bukkit.getPlayer(user.getUniqueId()))
                    .build(),
                    (p, ct) -> {
                        openPlayerInfo(player, user, () -> openPlayerList(player, back));
                        return true;
                    });
        });

        withBack(builder, back);
        builder.buildAsync(player, kdc.getPlugin().getScheduler().sync(), kdc.getPlugin().getScheduler().async());
    }

    // Player info

    public static void openPlayerInfo(PlatformPlayer player, User target) {
        openPlayerInfo(player, target, null);
    }

    static void openPlayerInfo(PlatformPlayer player, User target, Runnable back) {
        User user = player.getUser();

        kdc.getPlugin().getScheduler().async().execute(() -> {
            InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());
            ZoneId timeZone = kdc.getConfig().getTimeZone();

            builder.withItem(ItemStackBuilder.skull()
                    .withName(ChatColor.GOLD + target.getName())
                    .apply(b -> {
                        if (target.getKingdom() != null) {
                            b.withLore("");
                            b.withLore(text("menuInfoKingdom", colorify(target.getKingdom().getDisplay())));

                            if (target.getRank() != null) {
                                b.withLore(text("menuInfoRank", colorify(target.getRank().getDisplay())));
                            }

                            if (target.getJoinedKingdomAt() != null) {
                                ZonedDateTime zdt = target.getJoinedKingdomAt().atZone(timeZone);
                                b.withLore(text("menuInfoKingdomJoined", zdt.format(kdc.getConfig().getDateFormat())));
                            }
                        }
                    })
                    .apply(b -> {
                        b.withLore("");
                        if (kdc.getPlayer(target) != null) {
                            b.withLore(text("menuInfoLastSeen", "now"));
                            return;
                        }

                        ZonedDateTime zdt;
                        if (target.getLastOnlineAt() != null) {
                            zdt = target.getLastOnlineAt().atZone(timeZone);
                        } else {
                            zdt = target.getUpdatedAt().atZone(timeZone);
                        }

                        if (zdt.toLocalDate().equals(LocalDate.now(timeZone))) {
                            b.withLore(text("menuInfoLastSeen", zdt.format(kdc.getConfig().getTimeFormat())));
                        } else {
                            b.withLore(text("menuInfoLastSeen", zdt.format(kdc.getConfig().getDateFormat())));
                        }
                    })
                    .withLore(text("menuInfoFirstLogin", target.getCreatedAt().atZone(timeZone)
                            .format(kdc.getConfig().getDateFormat())))
                    .apply(b -> {
                        Player p = Bukkit.getPlayer(target.getUniqueId());
                        if (p != null) {
                            b.withSkullOwner(p);
                        }
                    })
                    .build()
            );

            if (target.getKingdom() != null) {
                builder.withItem(getKingdomItem(target.getKingdom()),
                        (p, ct) -> {
                            openKingdomInfo(player, target.getKingdom(), () -> openPlayerInfo(player, target, back));
                            return true;
                        }
                );

                if (player.hasPermission("kingdom.kick.other") || (
                        player.hasPermission("kingdom.kick")
                                && player.getUser().getKingdom() == target.getKingdom()
                                && user.getRank() != null
                                && (target.getRank() == null || user.getRank().getLevel() > target.getRank().getLevel())
                )) {
                    builder.withItem(ItemStackBuilder.of(Material.IRON_AXE)
                                    .withName(text("menuPlayerInfoItemKick"))
                                    .withLore(text("menuPlayerInfoItemLoreKick"))
                                    .build(),
                            (p, ct) -> {
                                openConfirmMenu(player, text("menuPlayerKickConfirmTitle", target.getName()), () -> {
                                    ((BukkitPlayer) player).getPlayer().chat("/k kick " + target.getName());
                                    openPlayerInfo(player, target, back);
                                }, () -> {
                                    openPlayerInfo(player, target, back);
                                });
                            }
                    );
                }

                if (!target.getKingdom().getRanks().isEmpty()
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
                                    .withName(text("menuPlayerInfoItemChangeRank"))
                                    .withLore(text("menuPlayerInfoItemLoreChangeRank"))
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
                if (player.getUser().getKingdom() != null && player.getUser().getKingdom().isInviteOnly()
                        && player.hasPermission("kingdom.invite")) {
                    builder.withItem(ItemStackBuilder.of(Material.COOKIE)
                                    .withName(text("menuPlayerInfoItemInvite"))
                                    .withLore(text("menuPlayerInfoItemLoreInvite"))
                                    .build(),
                            (p, ct) -> {
                                ((BukkitPlayer) player).getPlayer().chat("/k invite " + target.getName());
                            }
                    );
                }
            }

            withBack(builder, back);

            BukkitInventory inv = builder.build();
            kdc.getPlugin().getScheduler().sync().execute(() -> {
                player.openInventory(inv);
            });
        });
    }

    // Kingdom info

    public static void openKingdomInfo(PlatformPlayer player, Kingdom target) {
        openKingdomInfo(player, target, null);
    }

    static void openKingdomInfo(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + target.getName());

        builder.withItem(ItemStackBuilder.of(getKingdomItem(target))
                        .withLore("", text("menuKingdomInfoItemLore"))
                        .build(),
                (p, ct) -> {
                    return openKingdomEdit(player, target, () -> openKingdomInfo(player, target, back));
                });

        if (!target.getRanks().isEmpty() && (player.hasPermission("kingdom.ranks.list.other")
                || (player.getUser().getKingdom() == target && player.hasPermission("kingdom.ranks.list")))) {
            builder.withItem(ItemStackBuilder.of(Material.BOOK)
                            .withName(text("menuKingdomInfoItemRanks", target.getRanks().size() + ""))
                            .withLore(text("menuKingdomInfoItemLoreRanks"))
                            .build(), (p, cct) -> {
                        openKingdomRanksList(player, target, () -> openKingdomInfo(player, target, back));
                        return true;
                    }
            );
        }

        builder.withItem(ItemStackBuilder.of("GOLDEN_CHESTPLATE", "GOLD_CHESTPLATE")
                        .withName(text("menuKingdomInfoItemRelations"))
                        .withLore(text("menuKingdomInfoItemLoreRelations"))
                        .build(),
                (p, ct) -> {
                    openKingdomRelationsList(player, target, () -> openKingdomInfo(player, target, back));
                }
        );

        builder.withItem(ItemStackBuilder.skull()
                        .withName(text("menuKingdomInfoItemMembers", target.getMemberCount() + ""))
                        .apply(target.getMemberCount() > 0, (b) -> b.withLore(text("menuKingdomInfoItemLoreMembers")))
                        .build(),
                (p, ct) -> {
                    if (target.getMemberCount() == 0) {
                        return false;
                    }
                    openKingdomMembersList(player, target, () -> openKingdomInfo(player, target, back));
                    return true;
                }
        );

        if (player.getUser().getKingdom() == target || player.hasPermission("kingdom.spawn.other")) {
            builder.withItem(ItemStackBuilder.of("RED_BED", "BED")
                            .withName(text("menuKingdomInfoItemSpawn"))
                            .apply(b -> {
                                if (target.getSpawn() != null) {
                                    b.withLore("", ChatColor.GRAY + "X: " + (int) target.getSpawn().getX() + ", Y: " + (int) target.getSpawn().getY() + ", Z: " + (int) target.getSpawn().getZ());
                                } else {
                                    b.withLore(text("menuKingdomInfoItemLoreSpawnNotSet"));
                                }

                                if (player.hasPermission("kingdom.setspawn.other") || (player.hasPermission("kingdom.setspawn") && player.getUser().getKingdom() == target)) {
                                    b.withLore("", text("menuKingdomInfoItemLoreSpawnTeleport"),
                                            text("menuKingdomInfoItemLoreSpawnChange"));
                                } else if (player.hasPermission("kingdom.spawn") && target.getSpawn() != null) {
                                    b.withLore("", text("menuKingdomInfoItemLoreSpawnTeleport"));
                                }
                            }).build(),
                    (p, ct) -> {
                        if (ct == InventoryClickType.RIGHT) {
                            if (player.hasPermission("kingdom.setspawn.other")) {
                                openConfirmMenu(player, text("menuChangeKingdomSpawnOtherConfirmTitle", target.getName()), () -> {
                                    kdc.getCommandManager().dispatch(player, new String[]{"setspawn", target.getName()});
                                    openKingdomInfo(player, target, back);
                                }, () -> {
                                    openKingdomInfo(player, target, back);
                                });
                                return true;
                            } else if (player.hasPermission("kingdom.setspawn") && player.getUser().getKingdom() == target) {
                                openConfirmMenu(player, text("menuChangeKingdomSpawnConfirmTitle"), () -> {
                                    kdc.getCommandManager().dispatch(player, new String[]{"setspawn"});
                                    openKingdomInfo(player, target, back);
                                }, () -> {
                                    openKingdomInfo(player, target, back);
                                });
                                return true;
                            }
                        }

                        if (target.getSpawn() == null) {
                            return false;
                        }

                        if (target.getSpawn() != null && player.hasPermission("kingdom.spawn.other")) {
                            kdc.getCommandManager().dispatch(player, new String[]{"spawn", target.getName()});
                            return true;
                        }

                        if (player.hasPermission("kingdom.spawn") && player.getUser().getKingdom() == target) {
                            kdc.getCommandManager().dispatch(player, new String[]{"spawn"});
                            return true;
                        }
                        return false;
                    }
            );
        }

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Kingdom edit

    static boolean openKingdomEdit(PlatformPlayer player, Kingdom target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(text("menuKingdomEditTitle"));

        boolean hasItem = false;

        // Edit item
        if (player.hasPermission("kingdom.edit.item.other")
                || (player.hasPermission("kingdom.edit.item") && player.getUser().getKingdom() == target)) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.DIAMOND)
                            .withName(text("menuKingdomEditItemChangeItem"))
                            .withLore(text("menuKingdomEditItemLoreChangeItem"))
                            .build(),
                    (p, ct) -> {
                        openKingdomSelectItem(player, target, () -> openKingdomEdit(player, target, back));
                    }
            );
        }

        // Edit name
        if (player.hasPermission("kingdom.rename.other")
                || (player.hasPermission("kingdom.rename") && player.getUser().getKingdom() == target)) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.NAME_TAG)
                            .withName(text("menuKingdomEditItemRename"))
                            .withLore(text("menuKingdomEditItemLoreRename", ChatColor.WHITE + target.getName()))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuKingdomEditRenameQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target) {
                                kdc.getCommandManager().dispatch(player, new String[]{"rename", value});
                            } else {
                                kdc.getCommandManager().dispatch(player, new String[]{"rename", target.getName(), value});
                            }
                            openKingdomEdit(player, target, back);
                        }, () -> openKingdomEdit(player, target, back));
                    }
            );
        }

        // Edit display
        if (player.hasPermission("kingdom.edit.display.other")
                || (player.hasPermission("kingdom.edit.display") && player.getUser().getKingdom() == target)) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.PAPER)
                            .withName(text("menuKingdomEditItemChangeDisplay"))
                            .withLore(text("menuKingdomEditItemLoreChangeDisplay", colorify(target.getDisplay())))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuKingdomEditChangeDisplayQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target) {
                                kdc.getCommandManager().dispatch(player, new String[]{"edit", "display", value});
                            } else {
                                kdc.getCommandManager().dispatch(player, new String[]{"edit", "display", target.getName(), value});
                            }
                            openKingdomEdit(player, target, back);
                        }, () -> openKingdomEdit(player, target, back));
                    }
            );
        }

        // Edit prefix
        if (player.hasPermission("kingdom.edit.prefix.other")
                || (player.hasPermission("kingdom.edit.prefix") && player.getUser().getKingdom() == target)) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.GOLD_INGOT)
                            .withName(text("menuKingdomEditItemChangePrefix"))
                            .withLore(text("menuKingdomEditItemLoreChangePrefix", colorify(target.getPrefix())))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuKingdomEditChangePrefixQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target) {
                                kdc.getCommandManager().dispatch(player, new String[]{"edit", "prefix", value});
                            } else {
                                kdc.getCommandManager().dispatch(player, new String[]{"edit", "prefix", target.getName(), value});
                            }
                            openKingdomEdit(player, target, back);
                        }, () -> openKingdomEdit(player, target, back));
                    }
            );
        }

        // Edit suffix
        if (player.hasPermission("kingdom.edit.suffix.other")
                || (player.hasPermission("kingdom.edit.suffix") && player.getUser().getKingdom() == target)) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.IRON_INGOT)
                            .withName(text("menuKingdomEditItemChangeSuffix"))
                            .withLore(text("menuKingdomEditItemLoreChangeSuffix", colorify(target.getSuffix())))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuKingdomEditChangeSuffixQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target) {
                                kdc.getCommandManager().dispatch(player, new String[]{"edit", "suffix", value});
                            } else {
                                kdc.getCommandManager().dispatch(player, new String[]{"edit", "suffix", target.getName(), value});
                            }
                            openKingdomEdit(player, target, back);
                        }, () -> openKingdomEdit(player, target, back));
                    }
            );
        }

        // Edit max members
        if (player.hasPermission("kingdom.edit.max-members.other")
                || (player.hasPermission("kingdom.edit.max-members") && player.getUser().getKingdom() == target)) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.skull()
                            .withName(text("menuKingdomEditItemChangeMaxMembers"))
                            .apply(target.getMaxMembers() == 0, (b) -> b.withLore(text("menuKingdomEditItemLoreChangeMaxMembersUnlimited")))
                            .apply(target.getMaxMembers() != 0, (b) -> b.withLore(text("menuKingdomEditItemLoreChangeMaxMembers",
                                    target.getMaxMembers() + "")))
                            .build(),
                    (p, ct) -> {
                        int mm = target.getMaxMembers();
                        if (ct == InventoryClickType.LEFT) {
                            mm += 1;
                        } else if (ct == InventoryClickType.RIGHT) {
                            mm -= 1;
                        } else {
                            return false;
                        }

                        if (player.getUser().getKingdom() == target) {
                            ((BukkitPlayer) player).getPlayer().chat("/k edit max-members " + mm);
                        } else {
                            ((BukkitPlayer) player).getPlayer().chat("/k edit max-members " + target.getName() + " " + mm);
                        }
                        openKingdomEdit(player, target, back);
                        return true;
                    }
            );
        }

        // Invite only
        if (player.hasPermission("kingdom.edit.invite-only.other")
                || (player.hasPermission("kingdom.edit.invite-only") && player.getUser().getKingdom() == target)) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of("ENDER_EYE", "EYE_OF_ENDER")
                            .withName(text("menuKingdomEditItemToggleInviteOnly"))
                            .withLore(text("menuKingdomEditItemLoreToggleInviteOnly", target.isInviteOnly() + ""))
                            .build(),
                    (p, ct) -> {
                        if (player.getUser().getKingdom() == target) {
                            ((BukkitPlayer) player).getPlayer().chat("/k edit invite-only " + !target.isInviteOnly());
                        } else {
                            ((BukkitPlayer) player).getPlayer().chat("/k edit invite-only " + target.getName() + " " + !target.isInviteOnly());
                        }
                        openKingdomEdit(player, target, back);
                    }
            );
        }

        if (!hasItem) {
            return false;
        }

        withBack(builder, back);
        player.openInventory(builder.build());
        return true;
    }

    // Kingdom change item

    private static String upperCaseWords(String str) {
        List<String> words = new ArrayList<>();
        for (String word : str.split(Pattern.quote(" "))) {
            StringBuilder sb = new StringBuilder();
            sb.append(word.substring(0, 1).toUpperCase());
            if (word.length() > 1) {
                sb.append(word.substring(1).toLowerCase());
            }
            words.add(sb.toString());
        }
        return String.join(" ", words);
    }

    static void openKingdomSelectItem(PlatformPlayer player, Kingdom target, Runnable back) {
        PaginationBuilder builder = PaginationBuilder.create().withTitle(text("menuKingdomChangeItemTitle"));

        Map<ItemStack, String> items = new LinkedHashMap<>();

        // WOOL
        for (ItemStackBuilder.ItemColor color : ItemStackBuilder.ItemColor.values()) {
            items.put(ItemStackBuilder.wool(color).build(), upperCaseWords(color.name().replace("_", " ")) + " Wool");
        }

        // TERRACOTTA
        for (ItemStackBuilder.ItemColor color : ItemStackBuilder.ItemColor.values()) {
            items.put(ItemStackBuilder.terracotta(color).build(), upperCaseWords(color.name().replace("_", " ")) + " Terracotta");
        }

        // STAINED GLASS
        for (ItemStackBuilder.ItemColor color : ItemStackBuilder.ItemColor.values()) {
            items.put(ItemStackBuilder.glass(color).build(), upperCaseWords(color.name().replace("_", " ")) + " Glass");
        }

        items.put(ItemStackBuilder.of(Material.CHEST).build(), "Chest");
        items.put(ItemStackBuilder.of("STONE_BRICKS", "SMOOTH_BRICK").build(), "Stone Bricks");
        items.put(ItemStackBuilder.of("NETHER_BRICKS", "NETHER_BRICK").build(), "Nether Bricks");
        items.put(ItemStackBuilder.of("BRICKS", "BRICK").build(), "Bricks");
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

        List<ItemStack> items2 = new ArrayList<>();
        for (ItemStack item : items.keySet()) {
            items2.add(ItemStackBuilder.of(item.clone())
                    .withName(ChatColor.YELLOW + items.get(item))
                    .build());
        }

        builder.withItems(items2.size(), index -> new BukkitInventoryItem(items2.get(index), (p, ct) -> {
            target.setItem(new BukkitItem(items2.get(index)));
            kdc.saveAsync(target);
            back.run();
            return true;
        }));

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Kingdom player list

    static void openKingdomMembersList(PlatformPlayer player, Kingdom target, Runnable back) {
        kdc.getPlugin().getScheduler().async().execute(() -> {
            PaginationBuilder builder = PaginationBuilder.create().withTitle(text("menuKingdomMembersListTitle", target.getName()));

            // get members, database operation = must be executed async
            Map<UUID, String> members = target.getMembers();

            List<UUID> sorted = members.keySet().stream()
                    .sorted(Comparator.comparing(uuid -> kdc.getPlayer(uuid) == null))
                    .collect(Collectors.toList());

            builder.withItems(sorted.size(), index -> {
                UUID uuid = sorted.get(index);
                String name = members.get(uuid);
                boolean isOnline = kdc.getPlayer(uuid) != null;

                return new BukkitInventoryItem(ItemStackBuilder.skull()
                        .withName(isOnline ? ChatColor.GREEN + name : ChatColor.GRAY + name)
                        .apply(isOnline, (b) -> b.withSkullOwner(Bukkit.getPlayer(uuid)))
                        .build(),
                        (p, ct) -> {
                            kdc.getUser(uuid).thenAccept((u) -> {
                                kdc.getPlugin().getScheduler().sync().execute(() -> {
                                    openPlayerInfo(player, u, () -> openKingdomMembersList(player, target, back));
                                });
                            });
                            return true;
                        });
            });

            withBack(builder, back);
            builder.buildAsync(player, kdc.getPlugin().getScheduler().sync(), kdc.getPlugin().getScheduler().async());
        });
    }

    // Rank list

    static void openKingdomRanksList(PlatformPlayer player, Kingdom target, Runnable back) {
        PaginationBuilder builder = PaginationBuilder.create().withTitle(text("menuKingdomRanksListTitle", target.getName()));

        List<Rank> ranks = new ArrayList<>(target.getRanks());
        ranks.sort(Comparator.comparingInt(Rank::getLevel).reversed());

        builder.withItems(ranks.size(), index -> {
            Rank rank = ranks.get(index);
            return new BukkitInventoryItem(
                    ItemStackBuilder.of(getRankItem(rank))
                            .withLore("", text("menuKingdomRanksListItemLore"))
                            .build(),
                    (p, ct) -> openRankEdit(player, rank, () -> openKingdomRanksList(player, target, back)));
        });

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Rank edit

    static boolean openRankEdit(PlatformPlayer player, Rank target, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(ChatColor.DARK_GRAY + "Edit rank");

        boolean hasItem = false;

        // Edit name
        if (player.hasPermission("kingdom.ranks.rename.other")
                || (player.hasPermission("kingdom.ranks.rename") && player.getUser().getKingdom() == target.getKingdom())) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.NAME_TAG)
                            .withName(text("menuRankEditItemRename"))
                            .withLore(text("menuRankEditItemLoreRename", target.getName()))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuRankEditRenameQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target.getKingdom()) {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "rename", target.getName(), value});
                            } else {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "rename", target.getKingdom().getName(), target.getName(), value});
                            }
                            openRankEdit(player, target, back);
                        }, () -> openRankEdit(player, target, back));
                    }
            );
        }

        // Edit display
        if (player.hasPermission("kingdom.ranks.edit.display.other")
                || (player.hasPermission("kingdom.ranks.edit.display") && player.getUser().getKingdom() == target.getKingdom())) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.PAPER)
                            .withName(text("menuRankEditItemChangeDisplay"))
                            .withLore(text("menuRankEditItemLoreChangeDisplay", colorify(target.getDisplay())))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuRankEditChangeDisplayQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target.getKingdom()) {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "edit", "display", target.getName(), value});
                            } else {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "edit", "display", target.getKingdom().getName(), target.getName(), value});
                            }
                            openRankEdit(player, target, back);
                        }, () -> openRankEdit(player, target, back));
                    }
            );
        }

        // Edit prefix
        if (player.hasPermission("kingdom.ranks.edit.prefix.other")
                || (player.hasPermission("kingdom.ranks.edit.prefix") && player.getUser().getKingdom() == target.getKingdom())) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.GOLD_INGOT)
                            .withName(text("menuRankEditItemChangePrefix"))
                            .withLore(text("menuRankEditItemLoreChangePrefix", colorify(target.getPrefix())))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuRankEditChangePrefixQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target.getKingdom()) {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "edit", "prefix", target.getName(), value});
                            } else {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "edit", "prefix", target.getKingdom().getName(), target.getName(), value});
                            }
                            openRankEdit(player, target, back);
                        }, () -> openRankEdit(player, target, back));
                    }
            );
        }

        // Edit suffix
        if (player.hasPermission("kingdom.ranks.edit.suffix.other")
                || (player.hasPermission("kingdom.ranks.edit.suffix") && player.getUser().getKingdom() == target.getKingdom())) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.IRON_INGOT)
                            .withName(text("menuRankEditItemChangeSuffix"))
                            .withLore(text("menuRankEditItemLoreChangeSuffix", colorify(target.getSuffix())))
                            .build(),
                    (p, ct) -> {
                        kdc.getMessages().send(player, "menuRankEditChangeSuffixQuery");
                        startChatQuery(player, (value) -> {
                            if (player.getUser().getKingdom() == target.getKingdom()) {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "edit", "suffix", target.getName(), value});
                            } else {
                                kdc.getCommandManager().dispatch(player,
                                        new String[]{"ranks", "edit", "suffix", target.getKingdom().getName(), target.getName(), value});
                            }
                            openRankEdit(player, target, back);
                        }, () -> openRankEdit(player, target, back));
                    }
            );
        }

        // Edit max members
        if (player.hasPermission("kingdom.ranks.edit.max-members.other")
                || (player.hasPermission("kingdom.ranks.edit.max-members") && player.getUser().getKingdom() == target.getKingdom())) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.skull()
                            .withName(text("menuRankEditItemChangeMaxMembers"))
                            .apply(target.getMaxMembers() == 0, (b) -> b.withLore(text("menuRankEditItemLoreChangeMaxMembersUnlimited")))
                            .apply(target.getMaxMembers() != 0, (b) -> b.withLore(text("menuRankEditItemLoreChangeMaxMembers",
                                    target.getMaxMembers() + "")))
                            .build(),
                    (p, ct) -> {
                        int mm = target.getMaxMembers();
                        if (ct == InventoryClickType.LEFT) {
                            mm += 1;
                        } else if (ct == InventoryClickType.RIGHT) {
                            mm -= 1;
                        } else {
                            return false;
                        }

                        if (player.getUser().getKingdom() == target.getKingdom()) {
                            kdc.getCommandManager().dispatch(player,
                                    new String[]{"ranks", "edit", "max-members", target.getName(), mm + ""});
                        } else {
                            kdc.getCommandManager().dispatch(player,
                                    new String[]{"ranks", "edit", "max-members", target.getKingdom().getName(), target.getName(), mm + ""});
                        }
                        openRankEdit(player, target, back);
                        return true;
                    }
            );
        }

        // Invite only
        if (player.hasPermission("kingdom.ranks.edit.level.other")
                || (player.hasPermission("kingdom.ranks.edit.level") && player.getUser().getKingdom() == target.getKingdom())) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of(Material.BLAZE_ROD)
                            .withName(text("menuRankEditItemChangeLevel"))
                            .withLore(text("menuRankEditItemLoreChangeLevel",
                                    target.getLevel() + ""))
                            .build(),
                    (p, ct) -> {
                        int level = target.getLevel();
                        if (ct == InventoryClickType.LEFT) {
                            level += 1;
                        } else if (ct == InventoryClickType.RIGHT) {
                            level -= 1;
                        } else {
                            return false;
                        }

                        if (player.getUser().getKingdom() == target.getKingdom()) {
                            kdc.getCommandManager().dispatch(player,
                                    new String[]{"ranks", "edit", "level", target.getName(), level + ""});
                        } else {
                            kdc.getCommandManager().dispatch(player,
                                    new String[]{"ranks", "edit", "level", target.getKingdom().getName(), target.getName(), level + ""});
                        }
                        openRankEdit(player, target, back);
                        return true;
                    }
            );
        }

        if (!hasItem) {
            return false;
        }

        withBack(builder, back);
        player.openInventory(builder.build());
        return true;
    }

    // Rank selection

    static void openRankSelection(PlatformPlayer player, User target, Runnable back) {
        PaginationBuilder builder = PaginationBuilder.create().withTitle(text("menuChangePlayerRankTitle", target.getName()));

        List<Rank> ranks = new ArrayList<>();
        for (Rank rank : target.getKingdom().getRanks()) {
            if (rank == target.getRank() || (!player.hasPermission("kingdom.setrank.other")
                    && player.getUser().getKingdom() == target.getKingdom()
                    && rank.getLevel() >= player.getUser().getRank().getLevel())) {
                continue;
            }
            ranks.add(rank);
        }

        builder.withItems(ranks.size(), index -> {
            Rank rank = ranks.get(index);
            return new BukkitInventoryItem(getRankItem(rank), (p, ct) -> {
                openConfirmMenu(player, text("menuChangePlayerRankConfirmTitle", target.getName(), rank.getName()),
                        () -> {
                            ((BukkitPlayer) player).getPlayer().chat("/k setrank " + target.getName() + " " + rank.getName());
                            openPlayerInfo(player, target, back);
                        }, () -> openRankSelection(player, target, back));
                return true;
            });
        });

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
        PaginationBuilder builder = PaginationBuilder.create().withTitle(text("menuRelationsTitle", kingdom.getName()));

        Map<Kingdom, RelationType> relations = kdc.getRelations(kingdom).stream()
                .collect(Collectors.toMap(rel -> rel.getOther(kingdom), Relation::getType));

        List<Kingdom> kingdoms = relations.keySet().stream()
                .sorted(Comparator.comparing(kd -> RELATION_ORDER.get(relations.get(kd))))
                .collect(Collectors.toList());

        kingdoms.addAll(kdc.getKingdoms().stream()
                .filter(kd -> kd != kingdom && !kingdoms.contains(kd))
                .collect(Collectors.toSet()));

        builder.withItems(kingdoms.size(), index -> {
            Kingdom kd = kingdoms.get(index);
            RelationType rel = relations.getOrDefault(kd, RelationType.NEUTRAL);

            ItemStack item = ItemStackBuilder.of(getKingdomItem(kd))
                    .clearLore()
                    .withLore(ChatColor.GRAY + rel.name().substring(0, 1).toUpperCase() + rel.name().substring(1).toLowerCase())
                    .build();

            if (kingdom == player.getUser().getKingdom()) {
                return new BukkitInventoryItem(item, (p, ct) -> {
                    return openKingdomRelationSelect(player, kd, () -> openKingdomRelationsList(player, kingdom, back));
                });
            } else {
                return new BukkitInventoryItem(item);
            }
        });

        withBack(builder, back);
        player.openInventory(builder.build());
    }

    // Kingdom relation menu

    static boolean openKingdomRelationSelect(PlatformPlayer player, Kingdom kingdom, Runnable back) {
        InventoryBuilder builder = InventoryBuilder.create().withTitle(text("menuChangeRelationTitle", kingdom.getName()));

        boolean hasItem = false;

        Relation relation = kdc.getRelation(player.getUser().getKingdom(), kingdom);
        RelationType rel = relation != null ? relation.getType() : RelationType.NEUTRAL;

        Relation receivedRequest = kdc.getRelationRequest(kingdom, player.getUser().getKingdom());
        Relation sentRequest = kdc.getRelationRequest(player.getUser().getKingdom(), kingdom);

        if (rel != RelationType.ALLY && player.hasPermission("kingdom.ally")) {
            hasItem = true;
            if (sentRequest != null && sentRequest.getType() == RelationType.ALLY) {
                builder.withItem(ItemStackBuilder.of(Material.SLIME_BALL)
                        .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                        .withLore(text("menuChangeRelationItemLoreAllyRequested"))
                        .build()
                );
            } else {
                builder.withItem(ItemStackBuilder.of(Material.SLIME_BALL)
                                .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                                .apply(b -> {
                                    if (receivedRequest != null && receivedRequest.getType() == RelationType.ALLY) {
                                        b.withLore(text("menuChangeRelationItemLoreAllyAccept"));
                                    } else {
                                        b.withLore(text("menuChangeRelationItemLoreAllyRequest"));
                                    }
                                })
                                .build(),
                        (p, ct) -> {
                            kdc.getCommandManager().dispatch(player, new String[]{"ally", kingdom.getName()});
                            openKingdomRelationSelect(player, kingdom, back);
                        }
                );
            }
        }

        if (rel != RelationType.ENEMY && player.hasPermission("kingdom.enemy")) {
            hasItem = true;
            builder.withItem(ItemStackBuilder.of("FIRE_CHARGE", "FIREBALL")
                            .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                            .apply(b -> {
                                b.withLore(text("menuChangeRelationItemLoreEnemy"));
                            })
                            .build(),
                    (p, ct) -> {
                        kdc.getCommandManager().dispatch(player, new String[]{"enemy", kingdom.getName()});
                        openKingdomRelationSelect(player, kingdom, back);
                    }
            );
        }

        if (rel == RelationType.ENEMY && player.hasPermission("kingdom.truce")) {
            hasItem = true;
            if (sentRequest != null && sentRequest.getType() == RelationType.TRUCE) {
                builder.withItem(ItemStackBuilder.of(Material.MAGMA_CREAM)
                        .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                        .withLore(text("menuChangeRelationItemLoreTruceRequested"))
                        .build()
                );
            } else {
                builder.withItem(ItemStackBuilder.of(Material.MAGMA_CREAM)
                                .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                                .apply(b -> {
                                    if (receivedRequest != null && receivedRequest.getType() == RelationType.TRUCE) {
                                        b.withLore(text("menuChangeRelationItemLoreTruceAccept"));
                                    } else {
                                        b.withLore(text("menuChangeRelationItemLoreTruceRequest"));
                                    }
                                })
                                .build(),
                        (p, ct) -> {
                            kdc.getCommandManager().dispatch(player, new String[]{"truce", kingdom.getName()});
                            openKingdomRelationSelect(player, kingdom, back);
                        }
                );
            }
        }
        if (rel != RelationType.NEUTRAL && player.hasPermission("kingdom.neutral")) {
            hasItem = true;
            if (sentRequest != null && sentRequest.getType() == RelationType.NEUTRAL) {
                builder.withItem(ItemStackBuilder.of("SNOWBALL", "SNOW_BALL")
                        .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                        .withLore(text("menuChangeRelationItemLoreNeutralRequested"))
                        .build()
                );
            } else {
                builder.withItem(ItemStackBuilder.of("SNOWBALL", "SNOW_BALL")
                                .withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                                .apply(b -> {
                                    if (rel != RelationType.ALLY) {
                                        if (receivedRequest != null && receivedRequest.getType() == RelationType.NEUTRAL) {
                                            b.withLore(text("menuChangeRelationItemLoreNeutralAccept"));
                                        } else {
                                            b.withLore(text("menuChangeRelationItemLoreNeutralRequest"));
                                        }
                                    } else {
                                        b.withLore(text("menuChangeRelationItemLoreNeutralEndAlliance"));
                                    }
                                })
                                .build(),
                        (p, ct) -> {
                            kdc.getCommandManager().dispatch(player, new String[]{"neutral", kingdom.getName()});
                            openKingdomRelationSelect(player, kingdom, back);
                        }
                );
            }
        }

        if (!hasItem) {
            return false;
        }

        withBack(builder, back);
        player.openInventory(builder.build());
        return true;
    }

    // CONFIRM

    static void openConfirmMenu(PlatformPlayer player, String title, Runnable confirm, Runnable cancel) {
        BukkitInventory inv = new BukkitInventory(27, title);

        inv.setItem(11, ItemStackBuilder.of(Material.EMERALD_BLOCK)
                        .withName(text("menuConfirmAccept"))
                        .build(),
                (p, ct) -> {
                    confirm.run();
                    return true;
                }
        );

        inv.setItem(15, ItemStackBuilder.of(Material.REDSTONE_BLOCK)
                        .withName(text("menuConfirmCancel"))
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
                .withLore(text("menuInfoOnlineMembers",
                        kdc.getOnlineUsers().stream().filter(u -> u.getRank() == rank).count() + "",
                        rank.getMemberCount() + ""))
                .apply((b) -> {
                    if (rank.getMaxMembers() > 0) {
                        b.withLore(text("menuInfoMaxMembers", rank.getMaxMembers() + ""));
                    }
                })
                .build();
    }

    static ItemStack getKingdomItem(Kingdom kingdom) {
        ItemStackBuilder builder;
        if (kingdom.getItem() != null && kingdom.getItem().getHandle() != null) {
            builder = ItemStackBuilder.of((ItemStack) kingdom.getItem().getHandle());
        } else {
            builder = ItemStackBuilder.of("WHITE_BANNER", "BANNER");
        }

        return builder.withName(ChatColor.WHITE + colorify(kingdom.getDisplay()))
                .withLore("")
                .withLore(text("menuInfoOnlineMembers",
                        kdc.getOnlineUsers().stream().filter(u -> u.getKingdom() == kingdom).count() + "",
                        kingdom.getMemberCount() + ""))
                .apply((b) -> {
                    if (kingdom.getMaxMembers() > 0) {
                        b.withLore(text("menuInfoMaxMembers", kingdom.getMaxMembers() + ""));
                    }
                })
                .withLore(text("menuInfoCreatedAt", kingdom.getCreatedAt()
                        .atZone(kdc.getConfig().getTimeZone())
                        .format(kdc.getConfig().getDateFormat())))
                .build();
    }

    static void startChatQuery(PlatformPlayer player, Consumer<String> callback, Runnable cancel) {
        kdc.getMessages().send(player, "menuQueryCancel");
        player.set("MENU_CHAT_CALLBACK", callback);
        player.set("MENU_CHAT_CANCEL", cancel);
        player.closeInventory();
    }

}
