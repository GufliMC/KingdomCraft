package com.guflan.kingdomcraft.api.gui;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

import java.util.HashMap;
import java.util.Map;

public class Inventory<T extends InventoryItem<?>, U> {

    protected InventoryCallback callback;
    protected Map<Integer, T> items = new HashMap<>();

    protected U handle;

    public Inventory(U handle, InventoryCallback callback) {
        this.handle = handle;
        this.callback = callback;
    }

    public Inventory(U handle) {
        this(handle, null);
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

    public U getHandle() {
        return handle;
    }

    public T getItem(int slot) {
        return items.get(slot);
    }

    public void setItem(int slot, T item) {
        items.put(slot, item);
    }

}
