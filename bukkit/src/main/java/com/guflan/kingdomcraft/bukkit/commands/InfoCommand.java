/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.bukkit.commands;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.RelationType;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.api.gui.Inventory;
import com.guflan.kingdomcraft.api.gui.InventoryItem;
import com.guflan.kingdomcraft.bukkit.gui.BukkitInventory;
import com.guflan.kingdomcraft.bukkit.gui.BukkitInventoryItem;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
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

public class InfoCommand extends CommandBaseImpl {

    public InfoCommand(KingdomCraftImpl kdc) {
        super(kdc, "info", -1, true);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdInfoExplanation"));
        setPermissions("kingdom.info", "kingdom.info.other");
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

        ItemStack playerItem = getUserItem(user);

        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            inv.setItem(13, new BukkitInventoryItem(playerItem));
            player.openInventory(inv);
            return;
        }

        inv.setItem(12, new BukkitInventoryItem(playerItem));

        ItemStack kingdomItem = getKingdomItem(user);
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

        List<String> lore = new ArrayList<>();
        lore.add("");

        if ( user.getKingdom() == kingdom && kingdom.getSpawn() != null) {
            DecimalFormat df = new DecimalFormat("#");
            PlatformLocation loc = kingdom.getSpawn();
            lore.add(ChatColor.GRAY + "Spawn: " + ChatColor.GOLD + df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ()));
        }

        if ( kingdom.getMaxMembers() > 0 ) {
            lore.add(ChatColor.GRAY + "Total members: "
                    + ChatColor.GOLD + kingdom.getMemberCount()
                    + ChatColor.WHITE + " / "
                    + ChatColor.GOLD + kingdom.getMaxMembers());
        } else {
            lore.add(ChatColor.GRAY + "Total members: " + ChatColor.GOLD + kingdom.getMemberCount());
        }

        lore.add(ChatColor.GRAY + "Invite only: " + ChatColor.GOLD + kingdom.isInviteOnly());

        if ( kingdom.getDefaultRank() != null ) {
            lore.add(ChatColor.GRAY + "Default rank: " + ChatColor.GOLD + kingdom.getDefaultRank().getDisplay());
        }

        lore.add("");
        lore.add(ChatColor.GRAY + "Created at: " + ChatColor.GOLD + format(kingdom.getCreatedAt()));
        bannerMeta.setLore(lore);
        kingdomItem.setItemMeta(bannerMeta);
        inv.setItem(11, new BukkitInventoryItem(kingdomItem));

        // Enemies info

        ItemStack enemiesItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta enemiesMeta = enemiesItem.getItemMeta();
        enemiesMeta.setDisplayName(ChatColor.RED + "Enemies");
        List<String> enemiesLore = new ArrayList<>();

        kdc.getRelations(kingdom).stream().filter(rel -> rel.getType() == RelationType.ENEMY).forEach(rel -> {
            Kingdom target = rel.getKingdom1() == kingdom ? rel.getKingdom2() : rel.getKingdom1();
            enemiesLore.add(target.getDisplay());
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
        truceMeta.setDisplayName(ChatColor.GREEN + "Truce");
        List<String> truceLore = new ArrayList<>();

        kdc.getRelations(kingdom).stream().filter(rel -> rel.getType() == RelationType.TRUCE).forEach(rel -> {
            Kingdom target = rel.getKingdom1() == kingdom ? rel.getKingdom2() : rel.getKingdom1();
            truceLore.add(target.getDisplay());
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
        alliesMeta.setDisplayName(ChatColor.AQUA + "Allies");
        List<String> alliesLore = new ArrayList<>();

        kdc.getRelations(kingdom).stream().filter(rel -> rel.getType() == RelationType.ALLY).forEach(rel -> {
            Kingdom target = rel.getKingdom1() == kingdom ? rel.getKingdom2() : rel.getKingdom1();
            alliesLore.add(target.getDisplay());
        });

        if ( alliesLore.size() > 0 ) {
            alliesLore.add(0, "");
        }

        alliesMeta.setLore(alliesLore);
        alliesItem.setItemMeta(alliesMeta);
        inv.setItem(16, new BukkitInventoryItem(alliesItem));

        List<BukkitInventoryItem> rankItems = new ArrayList<>();
        kingdom.getRanks().stream().sorted(Comparator.comparingInt(r -> -r.getLevel())).forEach(rank -> {
            ItemStack rankItem = new ItemStack(Material.BOOK);
            ItemMeta rankMeta = rankItem.getItemMeta();
            rankMeta.setDisplayName(ChatColor.WHITE + kdc.getMessageManager().colorify(rank.getDisplay()));
            List<String> rankLore = new ArrayList<>();
            rankLore.add("");

            if ( rank.getMaxMembers() > 0 ) {
                rankLore.add(ChatColor.GRAY + "Total members: "
                        + ChatColor.GOLD + rank.getMemberCount()
                        + ChatColor.WHITE + " / "
                        + ChatColor.GOLD + rank.getMaxMembers());
            } else {
                rankLore.add(ChatColor.GRAY + "Total members: " + ChatColor.GOLD + rank.getMemberCount());
            }

            rankLore.add(ChatColor.GRAY + "Level: " + ChatColor.GOLD + rank.getLevel());
            rankMeta.setLore(rankLore);
            rankItem.setItemMeta(rankMeta);
            rankItems.add(new BukkitInventoryItem(rankItem));
        });

        placeItems(inv, 3, rankItems);
        player.openInventory(inv);
    }

    ItemStack getUserItem(User user) {
        ItemStack playerItem = new ItemStack(Material.SKULL_ITEM);

        SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
        skullMeta.setOwner(user.getName());

        skullMeta.setDisplayName(ChatColor.GREEN + user.getName());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Last seen: " + ChatColor.GOLD +
                (kdc.getPlayer(user) != null ? "now" : format(user.getUpdatedAt())));
        lore.add(ChatColor.GRAY + "First seen: " + ChatColor.GOLD + format(user.getCreatedAt()));

        skullMeta.setLore(lore);
        playerItem.setItemMeta(skullMeta);
        return playerItem;
    }

    ItemStack getKingdomItem(User user) {
        Kingdom kingdom = user.getKingdom();
        ItemStack kingdomItem = new ItemStack(Material.BANNER);

        BannerMeta bannerMeta = (BannerMeta) kingdomItem.getItemMeta();
        bannerMeta.setBaseColor(DyeColor.CYAN);

        bannerMeta.setDisplayName(ChatColor.WHITE + kdc.getMessageManager().colorify(kingdom.getDisplay()));
        List<String> lore = new ArrayList<>();

        if ( user.getRank() != null ) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Rank: " + ChatColor.GOLD + kdc.getMessageManager().colorify(user.getRank().getDisplay()));
        }

        bannerMeta.setLore(lore);
        kingdomItem.setItemMeta(bannerMeta);
        return kingdomItem;
    }

    private String format(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        return format.format(date);
    }

    private <T extends InventoryItem<?>> void placeItems(Inventory<T, ?> inv, int row, List<T> items) {
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
            placeItems(inv, row+1, items.subList(7, items.size()-1));
        }
    }
}
