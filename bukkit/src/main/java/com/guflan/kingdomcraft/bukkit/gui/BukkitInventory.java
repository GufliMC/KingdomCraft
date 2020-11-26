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

package com.guflan.kingdomcraft.bukkit.gui;

import com.guflan.kingdomcraft.api.gui.Inventory;
import com.guflan.kingdomcraft.api.gui.InventoryCallback;
import org.bukkit.Bukkit;

public class BukkitInventory extends Inventory<BukkitInventoryItem, org.bukkit.inventory.Inventory> {

    public BukkitInventory(org.bukkit.inventory.Inventory inv, InventoryCallback callback) {
        super(inv, callback);
    }

    public BukkitInventory(org.bukkit.inventory.Inventory inv) {
        super(inv);
    }

    public BukkitInventory(String name, int size, InventoryCallback callback) {
        this(Bukkit.createInventory(null, size, name), callback);
    }

    public BukkitInventory(String name, int size) {
        this(name, size, null);
    }

    @Override
    public void setItem(int slot, BukkitInventoryItem item) {
        super.setItem(slot, item);
        handle.setItem(slot, item.getHandle());
    }

}
