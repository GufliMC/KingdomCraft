package com.gufli.kingdomcraft.common.ebean.beans;

import io.ebean.Model;
import io.ebean.annotation.DbName;

import jakarta.persistence.MappedSuperclass;

@DbName("kingdomcraft")
@MappedSuperclass
public class BaseModel extends Model implements com.gufli.kingdomcraft.api.domain.Model {

    public BaseModel() {
        super("kingdomcraft");
    }

}
