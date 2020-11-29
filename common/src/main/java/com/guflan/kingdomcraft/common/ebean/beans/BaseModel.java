package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.Model;
import io.ebean.annotation.DbName;

import javax.persistence.MappedSuperclass;

@DbName("kingdomcraft")
@MappedSuperclass
public class BaseModel extends Model implements com.guflan.kingdomcraft.api.domain.Model {

    public BaseModel() {
        super("kingdomcraft");
    }

}
