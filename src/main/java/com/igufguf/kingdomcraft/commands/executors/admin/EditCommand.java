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
public class EditCommand extends CommandBase {

    private final KingdomCraft plugin;

    public EditCommand(KingdomCraft plugin) {
        super("edit", "kingdom.edit", false);

        this.plugin = plugin;

        plugin.getCmdHandler().register(this);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if ( args.length < 3 ) {
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

            // EDIT RANK OPTION

            if ( args.length < 4 ) {
                plugin.getMsg().send(sender, "cmdDefaultUsage");
                return false;
            }

            String option = args[2].toLowerCase();
            if ( !plugin.getApi().getKingdomManager().rankOptions.contains(option)) {
                plugin.getMsg().send(sender, "cmdEditRankInvalidOption", option);
                return false;
            }


            if ( rank.hasData(option) && rank.getData(option) instanceof List ) {

                // EDIT LIST
                if ( args.length < 5 ) {
                    plugin.getMsg().send(sender, "cmdDefaultUsage");
                    return false;
                }

                String value = "";
                for (int i = 4; i < args.length; i++) {
                    value += " " + args[i];
                }
                value = value.substring(1, value.length());

                Object realValue;
                if ( value.matches("[0-9]+") ) {
                    realValue = Long.valueOf(value);
                }
                else if ( value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ){
                    realValue = Boolean.valueOf(value);
                }
                else {
                    realValue = value;
                }

                String action = args[3];
                if ( action.equalsIgnoreCase("add") ) {
                    rank.addInList(option, realValue);
                    plugin.getMsg().send(sender, "cmdEditRankListAddSuccess", option, rank.getName(), value);
                } else if ( action.equalsIgnoreCase("remove") ) {
                    rank.delInList(option, realValue);
                    plugin.getMsg().send(sender, "cmdEditRankListRemoveSuccess", option, rank.getName(), value);
                } else {
                    plugin.getMsg().send(sender, "cmdDefaultUsage");
                    return false;
                }
            } else {

                // EDIT SINGLE VALUE

                String value = "";
                for (int i = 3; i < args.length; i++) {
                    value += " " + args[i];
                }
                value = value.substring(1, value.length());

                Object realValue;
                if ( value.matches("[0-9]+") ) {
                    realValue = Long.valueOf(value);
                }
                else if ( value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ){
                    realValue = Boolean.valueOf(value);
                }
                else {
                    realValue = value;
                }

                rank.setData(option, realValue);
                plugin.getMsg().send(sender, "cmdEditRankSuccess", option, rank.getName(), value);
            }

            plugin.getApi().getKingdomManager().saveKingdom(kingdom);
            return false;
        } else {

            // EDIT KINGDOM OPTION

            String option = args[1].toLowerCase();
            if ( !plugin.getApi().getKingdomManager().kingdomOptions.contains(option) ) {
                plugin.getMsg().send(sender, "cmdEditKingdomInvalidOption", option);
                return false;
            }

            if ( kingdom.hasData(option) && kingdom.getData(option) instanceof List ) {

                // EDIT LIST
                if ( args.length < 4 ) {
                    plugin.getMsg().send(sender, "cmdDefaultUsage");
                    return false;
                }

                String value = "";
                for (int i = 3; i < args.length; i++) {
                    value += " " + args[i];
                }
                value = value.substring(1, value.length());

                Object realValue;
                if ( value.matches("[0-9]+") ) {
                    realValue = Long.valueOf(value);
                }
                else if ( value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ){
                    realValue = Boolean.valueOf(value);
                }
                else {
                    realValue = value;
                }

                String action = args[2];
                if ( action.equalsIgnoreCase("add") ) {
                    kingdom.addInList(option, realValue);
                    plugin.getMsg().send(sender, "cmdEditKingdomListAddSuccess", option, kingdom.getName(), value);
                } else if ( action.equalsIgnoreCase("remove") ) {
                    kingdom.delInList(option, realValue);
                    plugin.getMsg().send(sender, "cmdEditKingdomListRemoveSuccess", option, kingdom.getName(), value);
                } else {
                    plugin.getMsg().send(sender, "cmdDefaultUsage");
                    return false;
                }

                plugin.getApi().getKingdomManager().saveKingdom(kingdom);
            } else {

                // EDIT SINGLE VALUE

                String value = "";
                for (int i = 2; i < args.length; i++) {
                    value += " " + args[i];
                }
                value = value.substring(1, value.length());

                Object realValue;
                if ( value.matches("[0-9]+") ) {
                    realValue = Long.valueOf(value);
                }
                else if ( value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ){
                    realValue = Boolean.valueOf(value);
                }
                else {
                    realValue = value;
                }

                kingdom.setData(option, realValue);
                plugin.getMsg().send(sender, "cmdEditKingdomSuccess", option, kingdom.getName(), value);
            }

            plugin.getApi().getKingdomManager().saveKingdom(kingdom);
            return false;
        }
    }

}
