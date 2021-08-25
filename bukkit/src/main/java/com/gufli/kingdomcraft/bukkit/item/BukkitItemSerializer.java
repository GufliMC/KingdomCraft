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

package com.gufli.kingdomcraft.bukkit.item;

import com.gufli.kingdomcraft.api.item.Item;
import com.gufli.kingdomcraft.api.item.ItemSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class BukkitItemSerializer implements Item.Serializer {

    public BukkitItemSerializer() {
        ItemSerializer.register(this);
    }

    @Override
    public Item deserialize(String value) {
        ItemStack is = stringToItem(value);
        if ( is == null ) {
            return null;
        }
        return new BukkitItem(is);
    }

    @Override
    public String serialize(Item item) {
        if ( item == null || item.getHandle() == null ) {
            return null;
        }
        return itemToString((ItemStack) item.getHandle());
    }

    // I know its dirty, leave me alone!
    public static String itemToString(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", itemStack);
        return config.saveToString();
    }

    public static ItemStack stringToItem(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(string);
        } catch (Exception ignored) {
            return null;
        }
        return config.getItemStack("item", null);
    }

}
