/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.api.gui;


import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInventory<T, U> implements Inventory<T, U> {

    protected InventoryCallback<U> callback;
    protected Map<Integer, InventoryItem<U>> items = new HashMap<>();

    protected T handle;

    public AbstractInventory(T handle, InventoryCallback<U> callback) {
        this.handle = handle;
        this.callback = callback;
    }

    public AbstractInventory(T handle) {
        this(handle, null);
    }

    public boolean dispatchClick(PlatformPlayer player, InventoryClickType clickType, int slot) {
        InventoryItem<U> item = items.get(slot);

        if ( callback != null ) {
            callback.onClick(player, clickType, slot, item);
        }

        if ( item != null ) {
            return item.dispatchClick(player, clickType);
        }
        return false;
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

    public T getHandle() {
        return handle;
    }

    public InventoryItem<U> getItem(int slot) {
        return items.get(slot);
    }

    public <V extends InventoryItem<U>> void setItem(int slot, V item) {
        items.put(slot, item);
    }

    public void removeItem(int slot) {
        items.remove(slot);
    }

    public Map<Integer, InventoryItem<U>> getItems() {
        return items;
    }

}
