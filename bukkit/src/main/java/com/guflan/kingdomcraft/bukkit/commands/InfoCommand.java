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
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.bukkit.gui.BukkitInventory;
import com.guflan.kingdomcraft.bukkit.gui.BukkitInventoryItem;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import javafx.scene.paint.Color;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class InfoCommand extends CommandBaseImpl {

    public InfoCommand(KingdomCraftImpl kdc) {
        super(kdc, "info", 0, true);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdInfoExplanation"));
        setPermissions("kingdom.info");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        String title = kdc.getMessageManager().getMessage("cmdInfoTitle");
        BukkitInventory inv = new BukkitInventory(title, 3 * 9);

        User user = kdc.getUser(player);
        ItemStack playerItem = getUserItem(user);

        Kingdom kingdom = user.getKingdom();
        if ( kingdom == null ) {
            inv.setItem(13, new BukkitInventoryItem(playerItem));
            player.openInventory(inv);
            return;
        }

        inv.setItem(12, new BukkitInventoryItem(playerItem));

        ItemStack kingdomItem = getKingdomItem(kingdom);
        inv.setItem(14, new BukkitInventoryItem(kingdomItem));

        player.openInventory(inv);
    }

    ItemStack getUserItem(User user) {
        ItemStack playerItem = new ItemStack(Material.SKULL_ITEM);

        SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
        skullMeta.setOwner(user.getName());

        skullMeta.setDisplayName(ChatColor.GREEN + user.getName());
        skullMeta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Last seen: " + ChatColor.GOLD + (kdc.getPlayer(user) != null ? "now" : format(user.getUpdatedAt())),
                ChatColor.GRAY + "First seen: " + ChatColor.GOLD + format(user.getCreatedAt())
        ));

        playerItem.setItemMeta(skullMeta);
        return playerItem;
    }

    ItemStack getKingdomItem(Kingdom kingdom) {
        ItemStack kingdomItem = new ItemStack(Material.BANNER);

        BannerMeta bannerMeta = (BannerMeta) kingdomItem.getItemMeta();
        bannerMeta.setBaseColor(DyeColor.CYAN);

        bannerMeta.setDisplayName(kdc.getMessageManager().colorify(kingdom.getDisplay()));
        bannerMeta.setLore(Arrays.asList(
                "",
                ChatColor.GRAY + "Created at: " + ChatColor.GOLD + format(kingdom.getCreatedAt())
        ));

        kingdomItem.setItemMeta(bannerMeta);
        return kingdomItem;
    }

    private String format(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.format(date);
    }
}
