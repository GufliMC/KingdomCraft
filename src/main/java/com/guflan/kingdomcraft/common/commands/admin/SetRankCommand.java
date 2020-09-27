package com.guflan.kingdomcraft.common.commands.admin;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.command.CommandSender;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class SetRankCommand extends DefaultCommandBase {

    public SetRankCommand(KingdomCraftPlugin plugin) {
        super(plugin, "setrank", 2);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {

        if ( args.length == 1 ) {
            if ( sender.hasPermission("kingdom.setrank.other") ) {
                return plugin.getPlayerManager().getOnlinePlayers().stream().filter(p -> p.getKingdom() != null).map(Player::getName).collect(Collectors.toList());
            } else if ( sender.hasPermission("kingdom.setrank") && !sender.isConsole()) {
                return plugin.getKingdomManager().getOnlineMembers(sender.getPlayer().getKingdom()).stream().map(Player::getName).collect(Collectors.toList());
            }
            return null;
        }

        Player player = plugin.getPlayerManager().getPlayer(args[0]);
        if ( player.getKingdom() == null ) {
            return null;
        }
        return player.getKingdom().getRanks().stream().map(Rank::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setrank") ) {
            plugin.getMessageManager().send(sender, "noPermission");
            return;
        }

        Player target = plugin.getPlayerManager().getPlayer(args[0]);
        if ( target == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultNoPlayer");
            return;
        }

        if ( target.getKingdom() == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultTargetNoKingdom", target.getName());
            return;
        }

        Kingdom kingdom = target.getKingdom();
        if ( !sender.isConsole() && !sender.hasPermission("kingdom.setrank.other") && sender.getPlayer().getKingdom() != kingdom ) {
            plugin.getMessageManager().send(sender, "noPermission");
            return;
        }

        Rank rank = kingdom.getRanks().stream().filter(r -> r.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);
        if ( rank == null ) {
            plugin.getMessageManager().send(sender, "cmdSetRankNotExist", args[1]);
            return;
        }

        target.setRank(rank);

        plugin.getMessageManager().send(target, "cmdSetRankTargetChange", rank.getName());
        plugin.getMessageManager().send(sender, "cmdSetRankSenderChange", target.getName(), rank.getName());
    }
}
