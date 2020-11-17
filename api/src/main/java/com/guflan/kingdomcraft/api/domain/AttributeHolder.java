package com.guflan.kingdomcraft.api.domain;

public interface AttributeHolder {

    Attribute getAttribute(String name);

    Attribute createAttribute(String name);

}
