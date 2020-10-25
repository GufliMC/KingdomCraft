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

package com.guflan.kingdomcraft.bukkit.gui;

import com.guflan.kingdomcraft.api.gui.InventoryItem;
import com.guflan.kingdomcraft.api.gui.InventoryItemCallback;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryItem extends InventoryItem<ItemStack> {

    public BukkitInventoryItem(ItemStack itemStack, InventoryItemCallback callback) {
        super(itemStack, callback);
    }

    public BukkitInventoryItem(ItemStack itemStack) {
        this(itemStack, null);
    }

}
