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
