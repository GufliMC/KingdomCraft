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

package com.gufli.kingdomcraft.bukkit.commands;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.RelationType;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformLocation;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.api.gui.Inventory;
import com.gufli.kingdomcraft.api.gui.InventoryItem;
import com.gufli.kingdomcraft.bukkit.gui.BukkitInventory;
import com.gufli.kingdomcraft.bukkit.gui.BukkitInventoryItem;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class InfoCommand extends CommandBase {

    private SimpleDateFormat dateFormat;

    public InfoCommand(KingdomCraftImpl kdc) {
        super(kdc, "info", -1, true);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdInfoExplanation"));
        setPermissions("kingdom.info", "kingdom.info.other");

        try {
            dateFormat = new SimpleDateFormat(kdc.getMessageManager().getMessage("cmdInfoDateFormat"));
        } catch (IllegalArgumentException ex) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 && player.hasPermission("kingdom.info.other") ) {
            List<String> result = kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
            result .addAll(kdc.getOnlinePlayers().stream().filter(p -> p != player)
                            .map(PlatformPlayer::getName).collect(Collectors.toList()));
            return result;
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;
        User user = kdc.getUser(player);

        if ( args.length == 0 || args[0].equalsIgnoreCase(player.getName()) ) {
            showUserInventory(player, user);
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom != null ) {
            if ( kingdom != user.getKingdom() && !player.hasPermission("kingdom.info.other") ) {
                kdc.getMessageManager().send(player, "cmdErrorNoPermission");
                return;
            }

            showKingdomInventory(player, kingdom);
            return;
        }

        if ( !player.hasPermission("kingdom.info.other") ) {
            kdc.getMessageManager().send(player, "cmdErrorNoPermission");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if ( target == null ) {
                    kdc.getMessageManager().send(player, "cmdErrorPlayerNotExist", args[0]);
                    return;
                }

                kdc.getPlugin().getScheduler().executeSync(() -> {
                    showUserInventory(player, target);
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private void showUserInventory(PlatformPlayer player, User user) {
        String title = ChatColor.DARK_GRAY + player.getName();
        BukkitInventory inv = new BukkitInventory(title, 3 * 9);

        ItemStack playerItem = new ItemStack(Material.SKULL_ITEM);

        SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
        skullMeta.setOwner(user.getName());

        skullMeta.setDisplayName(ChatColor.GREEN + user.getName());
        List<String> userLore = new ArrayList<>();
        userLore.add("");

        if ( kdc.getPlayer(user) != null ) {
            userLore.add(getText("LastSeen", getText("LastSeenNow")));
        } else {
            userLore.add(getText("LastSeen", dateFormat.format(user.getUpdatedAt())));
        }

        userLore.add(getText("FirstSeen", dateFormat.format(user.getCreatedAt())));

        skullMeta.setLore(userLore);
        playerItem.setItemMeta(skullMeta);

        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            inv.setItem(13, new BukkitInventoryItem(playerItem));
            player.openInventory(inv);
            return;
        }

        inv.setItem(12, new BukkitInventoryItem(playerItem));

        ItemStack kingdomItem = new ItemStack(Material.BANNER);

        BannerMeta bannerMeta = (BannerMeta) kingdomItem.getItemMeta();
        bannerMeta.setBaseColor(DyeColor.CYAN);

        bannerMeta.setDisplayName(ChatColor.WHITE + kdc.getMessageManager().colorify(kingdom.getDisplay()));
        List<String> kingdomLore = new ArrayList<>();

        if ( user.getRank() != null ) {
            kingdomLore.add("");
            kingdomLore.add(getText("Rank", user.getRank().getDisplay()));
        }

        bannerMeta.setLore(kingdomLore);
        kingdomItem.setItemMeta(bannerMeta);
        
        inv.setItem(14, new BukkitInventoryItem(kingdomItem, (p, clickType) -> {
            showKingdomInventory(player, kingdom);
            return true;
        }));

        player.openInventory(inv);
    }

    private void showKingdomInventory(PlatformPlayer player, Kingdom kingdom) {
        String title = ChatColor.DARK_GRAY + kdc.getMessageManager().colorify(kingdom.getDisplay());
        BukkitInventory inv = new BukkitInventory(title, 6 * 9);

        User user = kdc.getUser(player);

        // Kingdom info

        ItemStack kingdomItem = new ItemStack(Material.BANNER);
        BannerMeta bannerMeta = (BannerMeta) kingdomItem.getItemMeta();
        bannerMeta.setBaseColor(DyeColor.CYAN);
        bannerMeta.setDisplayName(ChatColor.WHITE + kdc.getMessageManager().colorify(kingdom.getDisplay()));

        List<String> kingdomLore = new ArrayList<>();
        kingdomLore.add("");

        if ( user.getKingdom() == kingdom && kingdom.getSpawn() != null) {
            DecimalFormat df = new DecimalFormat("#");
            PlatformLocation loc = kingdom.getSpawn();
            kingdomLore.add(getText("Spawn",df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ())));
        }

        if ( kingdom.getMaxMembers() > 0 ) {
            kingdomLore.add(getText("TotalMembersLimited", kingdom.getMemberCount() + "", kingdom.getMaxMembers() + ""));
        } else {
            kingdomLore.add(getText("TotalMembers", kingdom.getMemberCount() + ""));
        }

        kingdomLore.add(getText("InviteOnly", kingdom.isInviteOnly() + ""));

        if ( kingdom.getDefaultRank() != null ) {
            kingdomLore.add(getText("DefaultRank", kingdom.getDefaultRank().getDisplay()));
        }

        kingdomLore.add("");
        kingdomLore.add(getText("CreatedAt", dateFormat.format(kingdom.getCreatedAt())));

        bannerMeta.setLore(kingdomLore);
        kingdomItem.setItemMeta(bannerMeta);
        inv.setItem(11, new BukkitInventoryItem(kingdomItem));

        // Enemies info

        ItemStack enemiesItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta enemiesMeta = enemiesItem.getItemMeta();
        enemiesMeta.setDisplayName(getText("Enemies"));
        List<String> enemiesLore = new ArrayList<>();

        kdc.getRelations(kingdom).stream().filter(rel -> rel.getType() == RelationType.ENEMY).forEach(rel -> {
            Kingdom target = rel.getKingdom1() == kingdom ? rel.getKingdom2() : rel.getKingdom1();
            enemiesLore.add(kdc.getPlugin().colorify(target.getDisplay()));
        });

        if ( enemiesLore.size() > 0 ) {
            enemiesLore.add(0, "");
        }

        enemiesMeta.setLore(enemiesLore);
        enemiesItem.setItemMeta(enemiesMeta);
        inv.setItem(14, new BukkitInventoryItem(enemiesItem));

        // Truce info

        ItemStack truceItem = new ItemStack(Material.YELLOW_FLOWER);
        ItemMeta truceMeta = enemiesItem.getItemMeta();
        truceMeta.setDisplayName(getText("Truce"));
        List<String> truceLore = new ArrayList<>();

        kdc.getRelations(kingdom).stream().filter(rel -> rel.getType() == RelationType.TRUCE).forEach(rel -> {
            Kingdom target = rel.getKingdom1() == kingdom ? rel.getKingdom2() : rel.getKingdom1();
            truceLore.add(kdc.getPlugin().colorify(target.getDisplay()));
        });

        if ( truceLore.size() > 0 ) {
            truceLore.add(0, "");
        }

        truceMeta.setLore(truceLore);
        truceItem.setItemMeta(truceMeta);
        inv.setItem(15, new BukkitInventoryItem(truceItem));

        // Allies info

        ItemStack alliesItem = new ItemStack(Material.DIAMOND);
        ItemMeta alliesMeta = enemiesItem.getItemMeta();
        alliesMeta.setDisplayName(getText("Allies"));
        List<String> alliesLore = new ArrayList<>();

        kdc.getRelations(kingdom).stream().filter(rel -> rel.getType() == RelationType.ALLY).forEach(rel -> {
            Kingdom target = rel.getKingdom1() == kingdom ? rel.getKingdom2() : rel.getKingdom1();
            alliesLore.add(kdc.getPlugin().colorify(target.getDisplay()));
        });

        if ( alliesLore.size() > 0 ) {
            alliesLore.add(0, "");
        }

        alliesMeta.setLore(alliesLore);
        alliesItem.setItemMeta(alliesMeta);
        inv.setItem(16, new BukkitInventoryItem(alliesItem));

        // Add ranks
        List<BukkitInventoryItem> rankItems = new ArrayList<>();
        kingdom.getRanks().stream().sorted(Comparator.comparingInt(r -> -r.getLevel())).forEach(rank -> {
            ItemStack rankItem = new ItemStack(Material.BOOK);
            ItemMeta rankMeta = rankItem.getItemMeta();
            rankMeta.setDisplayName(ChatColor.WHITE + kdc.getMessageManager().colorify(rank.getDisplay()));
            List<String> rankLore = new ArrayList<>();
            rankLore.add("");

            if ( rank.getMaxMembers() > 0 ) {
                rankLore.add(getText("TotalMembersLimited", rank.getMemberCount() + "", rank.getMaxMembers() + ""));
            } else {
                rankLore.add(getText("TotalMembers", rank.getMemberCount() + ""));
            }

            rankLore.add(getText("Level", rank.getLevel() + ""));

            rankLore.add("");
            List<String> members = new ArrayList<>();
            for ( String member : rank.getMembers() ) {
                members.add(member);

                if ( members.size() + members.size() * 2 >= 30 ) {
                    rankLore.add(ChatColor.GRAY + String.join(", ", members) + ",");
                    members.clear();
                }
            }

            if ( !members.isEmpty() ) {
                rankLore.add(ChatColor.GRAY + String.join(", ", members));
            }

            rankMeta.setLore(rankLore);
            rankItem.setItemMeta(rankMeta);
            rankItems.add(new BukkitInventoryItem(rankItem));
        });

        placeItems(inv, 3, rankItems);
        player.openInventory(inv);
    }

    private <T extends InventoryItem<?>> void placeItems(Inventory<T, ?> inv, int row, List<T> items) {
        if ( row >= 6 ) {
            return;
        }

        int size = Math.min(items.size(), 7);

        int offset = row * 9;
        int slot = 4 - size/2;
        if ( size % 2 == 0 ) {
            for ( int i = 0; i < size; i++ ) {
                if ( i == size / 2 ) {
                    slot++;
                }
                inv.setItem(offset + slot, items.get(i));
                slot++;
            }
        } else {
            for ( int i = 0; i < size; i++ ) {
                inv.setItem(offset + slot, items.get(i));
                slot++;
            }
        }

        if ( items.size() > 7 ) {
            placeItems(inv, row+1, items.subList(7, items.size()));
        }

    }

    private String getText(String key) {
        return kdc.getMessageManager().getMessage("cmdInfo" + key, true);
    }

    private String getText(String key, String... placeholders) {
        return kdc.getMessageManager().getMessage("cmdInfo" + key, true, placeholders);
    }
}
