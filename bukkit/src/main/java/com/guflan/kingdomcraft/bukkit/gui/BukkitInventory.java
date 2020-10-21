package com.guflan.kingdomcraft.bukkit.gui;

import com.guflan.kingdomcraft.api.gui.Inventory;
import com.guflan.kingdomcraft.api.gui.InventoryCallback;
import org.bukkit.Bukkit;

public class BukkitInventory extends Inventory<BukkitInventoryItem, org.bukkit.inventory.Inventory> {

    public BukkitInventory(String name, int size, InventoryCallback callback) {
        super(Bukkit.createInventory(null, size, name), callback);
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
