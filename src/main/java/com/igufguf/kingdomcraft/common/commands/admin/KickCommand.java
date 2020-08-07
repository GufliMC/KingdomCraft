package com.igufguf.kingdomcraft.common.commands.admin;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.commands.DefaultCommandBase;
import org.bukkit.ChatColor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KickCommand extends DefaultCommandBase {

    public KickCommand(KingdomCraftPlugin plugin) {
        super(plugin, "kick", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player target = plugin.getPlayerManager().getPlayer(args[0]);
        if ( target == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultNoPlayer");
            return;
        }
        Kingdom kingdom = target.getKingdom();

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
