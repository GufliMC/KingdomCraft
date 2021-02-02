package com.gufli.kingdomcraft.api.gui;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public interface Inventory<T, U> {

    boolean dispatchClick(PlatformPlayer player, InventoryClickType clickType, int slot);

    void dispatchClose(PlatformPlayer player);

    void dispatchOpen(PlatformPlayer player);

    T getHandle();

    InventoryItem<U> getItem(int slot);

    <V extends InventoryItem<U>> void setItem(int slot, V item);

    void removeItem(int slot);

}
