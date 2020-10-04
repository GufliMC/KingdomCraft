package com.guflan.kingdomcraft.common.commands.admin;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class SetKingdomCommand extends DefaultCommandBase {

    public SetKingdomCommand(KingdomCraftPlugin plugin) {
        super(plugin, "setkingdom", 2);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setkingdom") ) {
            return null;
        }
        if ( args.length == 1 ) {
            return plugin.getPlayerManager().getOnlinePlayers().stream().map((p) -> p.getPlayer().getName()).collect(Collectors.toList());
        }

        return plugin.getKingdomManager().getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setkingdom") ) {
            plugin.getMessageManager().send(sender, "noPermission");
        }

        User target = plugin.getPlayerManager().getPlayer(args[0]);
        if ( target == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultNoPlayer");
            return;
        }

        Kingdom kingdom = plugin.getKingdomManager().getKingdom(args[1]);
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        plugin.getPlayerManager().leaveKingdom(target);
        plugin.getPlayerManager().joinKingdom(target, kingdom);

        plugin.getMessageManager().send(target, "cmdSetKingdomTarget", kingdom.getName());
        plugin.getMessageManager().send(sender, "cmdSetKingdomSender", target.getName(), kingdom.getName());
    }
}
