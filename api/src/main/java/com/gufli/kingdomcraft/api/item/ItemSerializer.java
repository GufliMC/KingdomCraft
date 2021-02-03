package com.gufli.kingdomcraft.api.item;

public class ItemSerializer {

    private static Item.Serializer defaultSerializer;

    public static void register(Item.Serializer serializer) {
        defaultSerializer = serializer;
    }

    public static Item deserialize(String value) {
        return defaultSerializer.deserialize(value);
    }

    public static String serialize(Item item) {
        return defaultSerializer.serialize(item);
    }

    private ItemSerializer() {}

}