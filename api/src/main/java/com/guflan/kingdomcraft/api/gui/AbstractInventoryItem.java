package com.guflan.kingdomcraft.api.gui;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

public abstract class AbstractInventoryItem {

    protected InventoryItemCallback callback;

    public AbstractInventoryItem() {

    }

    public AbstractInventoryItem(InventoryItemCallback callback) {
        this.callback = callback;
    }

    public void dispatchClick(PlatformPlayer player, InventoryClickType type) {
        if ( callback == null ) {
            return;
        }
        callback.onClick(player, type);
    }
}
