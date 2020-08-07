package com.igufguf.kingdomcraft.common.commands.member;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.KingdomInvite;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.commands.DefaultCommandBase;
import org.bukkit.Bukkit;

public class InviteCommand extends DefaultCommandBase {

    public InviteCommand(KingdomCraftPlugin plugin) {
        super(plugin, "invite", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = sender.getPlayer();
        if (player.getKingdom() == null) {
            plugin.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Player target = plugin.getPlayerManager().getPlayer(args[0]);
        if ( target == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultNotOnline");
            return;
        }

        if ( target.getKingdom() == player.getKingdom() ) {
            plugin.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
            return;
        }

        if ( target.isInvitedFor(player.getKingdom()) ) {
            plugin.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
            return;
        }

        plugin.getPlayerManager().addInvite(player, target);
        plugin.getMessageManager().send(target, "cmdInviteTarget", player.getKingdom().getName());
        plugin.getMessageManager().send(sender, "cmdInviteSender", player.getKingdom().getName());
    }
}
