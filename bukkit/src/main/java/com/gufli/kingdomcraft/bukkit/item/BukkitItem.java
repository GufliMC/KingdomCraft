package com.gufli.kingdomcraft.bukkit.item;

import com.gufli.kingdomcraft.api.item.Item;
import org.bukkit.inventory.ItemStack;

public class BukkitItem implements Item {

    private final ItemStack handle;

    public BukkitItem(ItemStack handle) {
        this.handle = handle;
    }

    @Override
    public Object getHandle() {
        return handle.clone();
    }
}
