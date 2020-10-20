package com.guflan.kingdomcraft.api.gui;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

@FunctionalInterface
public interface InventoryItemCallback {
    void onClick(PlatformPlayer player, InventoryClickType clickType);
}
