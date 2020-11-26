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

import com.guflan.kingdomcraft.api.gui.*;

public class BukkitInventoryFactory implements InventoryFactory {
    
    public BukkitInventoryFactory() {
        InventoryFactoryRegistry.register(this);
    }
    
    @Override
    public Inventory<?, ?> createInventory(Object handle) {
        return createInventory(handle, null);
    }

    @Override
    public Inventory<?, ?> createInventory(Object handle, InventoryCallback callback) {
        if ( !(handle instanceof org.bukkit.inventory.Inventory) ) {
            throw new IllegalArgumentException("handle must be of type org.bukkit.inventory.Inventory");
        }
        return new BukkitInventory((org.bukkit.inventory.Inventory) handle, callback);
    }

    @Override
    public InventoryItem<?> createItem(Object handle) {
        return createItem(handle, null);
    }

    @Override
    public InventoryItem<?> createItem(Object handle, InventoryItemCallback callback) {
        if ( !(handle instanceof org.bukkit.inventory.ItemStack) ) {
            throw new IllegalArgumentException("handle must be of type org.bukkit.inventory.ItemStack");
        }
        return new BukkitInventoryItem((org.bukkit.inventory.ItemStack) handle, callback);
    }
}
