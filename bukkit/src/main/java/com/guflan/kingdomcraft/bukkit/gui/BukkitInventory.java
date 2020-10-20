package com.guflan.kingdomcraft.bukkit.gui;

import com.guflan.kingdomcraft.api.gui.AbstractInventory;
import com.guflan.kingdomcraft.api.gui.InventoryCallback;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class BukkitInventory extends AbstractInventory<BukkitInventoryItem> {

    private final Inventory inventory;

    public BukkitInventory(String name, int size, InventoryCallback callback) {
        super(callback);
        inventory = Bukkit.createInventory(null, size, name);
    }

    public BukkitInventory(String name, int size) {
        this(name, size, null);
    }

    @Override
    public void setItem(int slot, BukkitInventoryItem item) {
        super.setItem(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    public Inventory getInventory() {
        return inventory;
    }
}
