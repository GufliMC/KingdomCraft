package com.gufli.kingdomcraft.common.ebean.beans;

import com.gufli.kingdomcraft.api.item.Item;
import com.gufli.kingdomcraft.api.item.ItemSerializer;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ItemConverter implements AttributeConverter<Item, String> {

    @Override
    public String convertToDatabaseColumn(Item attribute) {
        return ItemSerializer.serialize(attribute);
    }

    @Override
    public Item convertToEntityAttribute(String dbData) {
        return ItemSerializer.deserialize(dbData);
    }

}
