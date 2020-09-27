package com.guflan.kingdomcraft.common.ebean.beans;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
public abstract class BaseModel extends Model {

  @Id
  long id;

  @WhenCreated
  Instant created_at;

  @WhenModified
  Instant updated_at;

}