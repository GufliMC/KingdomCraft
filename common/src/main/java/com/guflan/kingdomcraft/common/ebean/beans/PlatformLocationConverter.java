package com.guflan.kingdomcraft.common.ebean.beans;

import com.guflan.kingdomcraft.api.entity.PlatformLocation;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PlatformLocationConverter implements AttributeConverter<PlatformLocation, String> {

    @Override
    public String convertToDatabaseColumn(PlatformLocation attribute) {
        return attribute.serialize();
    }

    @Override
    public PlatformLocation convertToEntityAttribute(String dbData) {
        return PlatformLocation.deserialize(dbData);
    }
}
