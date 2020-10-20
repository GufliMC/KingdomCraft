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

package com.guflan.kingdomcraft.bukkit.placeholders;

import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.apache.commons.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class PlaceholderReplacer {

    public PlaceholderReplacer(KingdomCraftBukkitPlugin plugin) {
        PlaceholderManager pm = plugin.getKdc().getPlaceholderManager();

        pm.addPlaceholderReplacer((player, placeholder) -> Bukkit.getPlayer(player.getUniqueId())
                    .getLocation().getWorld().toString(),
            "world");

        pm.addPlaceholderReplacer((player, placeholder) -> {
                ItemStack stack = Bukkit.getPlayer(player.getUniqueId()).getItemInHand();
                if ( stack.getItemMeta() == null || stack.getItemMeta().getDisplayName() == null ) {
                    String name = stack.getType().toString().replace("_", " ");
                    return WordUtils.capitalizeFully(name, ' ');
                }
                return stack.getItemMeta().getDisplayName();
            },
            "item", "weapon");
    }

}
