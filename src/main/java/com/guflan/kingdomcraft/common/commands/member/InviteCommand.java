package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.concurrent.ExecutionException;

public class InviteCommand extends DefaultCommandBase {

    public InviteCommand(KingdomCraft kdc) {
        super(kdc, "invite", 1, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.invite") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        User user = kdc.getUser((Player) sender);

        if (user.getKingdom() == null) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultNoPlayer");
                    return;
                }

                if (target.getKingdom() == user.getKingdom()) {
                    kdc.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
                    return;
                }

                if (target.hasInvite(user.getKingdom())) {
                    kdc.getMessageManager().send(sender, "cmdInviteAlready", target.getName());
                    return;
                }

                target.addInvite(user);
                kdc.save(target);

                Player targetPlayer = kdc.getPlayer(target);
                if (targetPlayer != null ) {
                    kdc.getMessageManager().send(targetPlayer, "cmdInviteTarget", user.getKingdom().getName());
                }

                kdc.getMessageManager().send(sender, "cmdInviteSender", target.getName(), user.getKingdom().getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
