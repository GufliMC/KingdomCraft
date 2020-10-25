/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

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

    public boolean dispatchClick(PlatformPlayer player, InventoryClickType clickType, int slot) {
        T item = items.get(slot);

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
