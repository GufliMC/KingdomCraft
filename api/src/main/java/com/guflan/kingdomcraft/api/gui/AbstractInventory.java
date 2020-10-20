package com.guflan.kingdomcraft.api.gui;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInventory<T extends AbstractInventoryItem> {

    protected InventoryCallback callback;
    protected Map<Integer, T> items = new HashMap<>();

    public AbstractInventory() {}

    public AbstractInventory(InventoryCallback callback) {
        this.callback = callback;
    }

    public void dispatchClick(PlatformPlayer player, InventoryClickType clickType, int slot) {
        T item = items.get(slot);

        if ( callback != null ) {
            callback.onClick(player, clickType, slot, item);
        }

        if ( item != null ) {
            item.dispatchClick(player, clickType);
        }
    }

    public void dispatchClose(PlatformPlayer player) {
        if ( callback != null ) {
            callback.onClose(player);
        }
    }

    public void dispatchOpen(PlatformPlayer player) {
        if ( callback != null ) {
            callback.onOpen(player);
        }
    }

    public AbstractInventoryItem getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot, T item) {
        items.put(slot, item);
    }

}
