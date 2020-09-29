package com.guflan.kingdomcraft.common.commands.admin;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class KickCommand extends DefaultCommandBase {

    public KickCommand(KingdomCraftPlugin plugin) {
        super(plugin, "kick", 1);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( sender.hasPermission("kingdom.kick.other") ) {
            return plugin.getPlayerManager().getOnlinePlayers().stream().map((p) -> p.getPlayer().getName()).collect(Collectors.toList());
        } else if ( sender.hasPermission("kingdom.kick") && sender.getPlayer().getKingdom() != null ) {
            return sender.getPlayer().getKingdom().getMembers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player target = plugin.getPlayerManager().getPlayer(args[0]);
        if ( target == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultNoPlayer");
            return;
        }

        Kingdom kingdom = target.getKingdom();
        if ( kingdom == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultTargetNoKingdom", target.getName());
            return;
        }

        if ( !sender.isConsole() ) {
            Player player = sender.getPlayer();
            // kick other kingdom
            if (kingdom != player.getKingdom() && !sender.hasPermission("kingdom.kick.other")) {
                plugin.getMessageManager().send(sender, "noPermissionCmd");
                return;
            }

            // kick own kingdom
            if (kingdom == player.getKingdom() && !sender.hasPermission("kingdom.kick")) {
                plugin.getMessageManager().send(sender, "noPermissionCmd");
                return;
            }
        }

        plugin.getPlayerManager().leaveKingdom(target);
        plugin.getMessageManager().send(target, "cmdKickTarget", kingdom.getName());
        plugin.getMessageManager().send(sender, "cmdKickSender", target.getName(), kingdom.getName());
    }
}
