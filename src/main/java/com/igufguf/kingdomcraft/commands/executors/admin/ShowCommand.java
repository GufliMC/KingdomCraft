package com.igufguf.kingdomcraft.commands.executors.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Joris on 28/07/2018 in project KingdomCraft.
 */
public class ShowCommand extends CommandBase {

    private final KingdomCraft plugin;

    public ShowCommand(KingdomCraft plugin) {
        super("show", "kingdom.show", false);

        this.plugin = plugin;

        plugin.getCmdHandler().register(this);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if ( args.length < 2 ) {
            plugin.getMsg().send(sender, "cmdDefaultUsage");
            return false;
        }

        String name = args[0];
        KingdomObject kingdom = plugin.getApi().getKingdomManager().getKingdom(name);
        if ( kingdom == null ) {
            plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", name);
            return false;
        }

        KingdomRank rank = kingdom.getRank(args[1]);
        if ( rank != null ) {

            // RANK OPTION

            if ( args.length < 3 ) {
                plugin.getMsg().send(sender, "cmdDefaultUsage");
                return false;
            }

            String option = args[2].toLowerCase();
            if ( !plugin.getApi().getKingdomManager().rankOptions.contains(option)) {
                plugin.getMsg().send(sender, "cmdShowRankInvalidOption", option);
                return false;
            }

            if ( !rank.hasData(option) ) {
                plugin.getMsg().send(sender, "cmdShowRankOptionNotSet", option, rank.getName());
                return false;
            }

            if ( rank.getData(option) instanceof List ) {

                String s = "";
                for ( String value : rank.getList(option, String.class) ) {
                    s += ", " + value;
                }
                s = s.substring(2, s.length());
                plugin.getMsg().send(sender, "cmdShowRankOptionList", option, rank.getName(), s);
            } else {

                plugin.getMsg().send(sender, "cmdShowRankOption", option, rank.getName(), rank.getString(option));
            }

            return false;
        } else {

            // KINGDOM OPTION

            String option = args[1].toLowerCase();
            if ( !plugin.getApi().getKingdomManager().kingdomOptions.contains(option) ) {
                plugin.getMsg().send(sender, "cmdEditKingdomInvalidOption", option);
                return false;
            }

            if ( !kingdom.hasData(option) ) {
                plugin.getMsg().send(sender, "cmdShowKingdomOptionNotSet", option, kingdom.getName());
                return false;
            }

            if ( kingdom.getData(option) instanceof List ) {

                String s = "";
                for ( String value : kingdom.getList(option, String.class) ) {
                    s += ", " + value;
                }
                s = s.substring(2, s.length());
                plugin.getMsg().send(sender, "cmdShowKingdomOptionList", option, kingdom.getName(), s);
            } else {
                plugin.getMsg().send(sender, "cmdShowKingdomOption", option, kingdom.getName(), kingdom.getString(option));
            }
            return false;
        }
    }

}
