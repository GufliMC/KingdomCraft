package com.guflan.kingdomcraft.api.gui;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

public class InventoryItem<T> {

    protected T handle;
    protected InventoryItemCallback callback;

    public InventoryItem(T handle, InventoryItemCallback callback) {
        this.handle = handle;
        this.callback = callback;
    }

    public InventoryItem(T handle) {
        this(handle, null);
    }

    public boolean dispatchClick(PlatformPlayer player, InventoryClickType type) {
        if ( callback == null ) {
            return false;
        }
        return callback.onClick(player, type);
    }

    public T getHandle() {
        return handle;
    }
}
