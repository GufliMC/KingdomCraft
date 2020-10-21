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

package com.guflan.kingdomcraft.api.entity;

import com.guflan.kingdomcraft.api.gui.Inventory;

import java.util.UUID;

public interface PlatformPlayer extends PlatformSender {

    String CUSTOM_GUI_KEY = "CUSTOM_GUI";

    UUID getUniqueId();

    String getName();

    void teleport(PlatformLocation location);

    PlatformLocation getLocation();

    // gui

    default Inventory<?, ?> getInventory() {
        return has(CUSTOM_GUI_KEY) ? get(CUSTOM_GUI_KEY, Inventory.class) : null;
    }

    default void openInventory(Inventory<?, ?> inventory) {
        set(CUSTOM_GUI_KEY, inventory);
    }

    // cache

    void set(String key, Object value);

    boolean has(String key);

    Object get(String key);

    <T> T get(String key, Class<T> clazz);
}
