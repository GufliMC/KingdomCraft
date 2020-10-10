package com.guflan.kingdomcraft.common.ebean;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import java.io.IOException;

public class GenerateDbMigration {

  /**
   * Generate the next "DB schema DIFF" migration.
   */
  public static void main(String[] args) throws IOException {


    DbMigration dbMigration = DbMigration.create();
    dbMigration.addPlatform(Platform.H2, "h2");
    dbMigration.addPlatform(Platform.MYSQL, "mysql");
    dbMigration.addPlatform(Platform.SQLITE, "sqlite");
    dbMigration.addPlatform(Platform.POSTGRES, "postgres");

    dbMigration.generateMigration();
  }
}