package com.gufli.kingdomcraft.common.commands.admin;

import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;
import io.ebean.DB;
import io.ebean.Database;

import java.io.File;

public class SqlDumpCommand extends CommandBase {

    public SqlDumpCommand(KingdomCraftImpl kdc) {
        super(kdc, "sqldump", 0);
        setPermissions("kingdom.sqldump");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        File file = new File(kdc.getPlugin().getDataFolder(), "sqldump.sql");

        Database db = DB.byName("kingdomcraft");
        db.sqlQuery("SCRIPT TO '" + file.getAbsolutePath() + "';").findList();

        kdc.getMessages().send(sender, "cmdSqlDump", file.getName());
    }
}
