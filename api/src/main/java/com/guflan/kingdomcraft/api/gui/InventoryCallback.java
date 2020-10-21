package com.guflan.kingdomcraft.api.gui;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;

public interface InventoryCallback {

    void onOpen(PlatformPlayer player);

    void onClose(PlatformPlayer player);

    void onClick(PlatformPlayer player, InventoryClickType clickType, int slot, InventoryItem item);

}
