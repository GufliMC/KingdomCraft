package com.guflan.kingdomcraft.bukkit.gui;

import com.guflan.kingdomcraft.api.gui.AbstractInventoryItem;
import com.guflan.kingdomcraft.api.gui.InventoryItemCallback;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryItem extends AbstractInventoryItem {

    private final ItemStack itemStack;

    public BukkitInventoryItem(ItemStack itemStack, InventoryItemCallback callback) {
        super(callback);
        this.itemStack = itemStack;
    }

    public BukkitInventoryItem(ItemStack itemStack) {
        this(itemStack, null);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
