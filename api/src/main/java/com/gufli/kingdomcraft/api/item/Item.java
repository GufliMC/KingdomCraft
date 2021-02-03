package com.gufli.kingdomcraft.api.item;

public interface Item {

    Object getHandle();

    interface Serializer {

        Item deserialize(String value);

        String serialize(Item item);

    }

}
