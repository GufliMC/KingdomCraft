package com.gufli.kingdomcraft.api.gui;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public interface InventoryItem<T> {

    boolean dispatchClick(PlatformPlayer player, InventoryClickType type);

    T getHandle();

}
