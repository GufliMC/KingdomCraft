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
        return new BukkitItem(stringToItem(value));
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
