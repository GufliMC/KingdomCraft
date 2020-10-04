package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class InviteCommand extends DefaultCommandBase {

    public InviteCommand(KingdomCraftPlugin plugin) {
        super(plugin, "invite", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.invite") ) {
            plugin.getMessageManager().send(sender, "noPermission");
        }

        User player = sender.getPlayer();
        if (player.getKingdom() == null) {
            plugin.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        User target = plugin.getPlayerManager().getPlayer(args[0]);
        if ( target == null ) {
            plugin.getMessageManager().send(sender, "cmdDefaultNotOnline");
            return;
        }

        if ( target.getKingdom() == player.getKingdom() ) {
            plugin.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
            return;
        }

        if ( target.hasInvite(player.getKingdom()) ) {
            plugin.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
            return;
        }

        plugin.getPlayerManager().addInvite(player, target);
        plugin.getMessageManager().send(target, "cmdInviteTarget", player.getKingdom().getName());
        plugin.getMessageManager().send(sender, "cmdInviteSender", player.getKingdom().getName());
    }
}
