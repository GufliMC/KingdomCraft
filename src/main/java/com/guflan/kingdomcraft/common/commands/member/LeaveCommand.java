package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class LeaveCommand extends DefaultCommandBase {

    public LeaveCommand(KingdomCraft kdc) {
        super(kdc, "leave", 0, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.leave") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        User user = kdc.getUser((Player) sender);
        if (user.getKingdom() == null) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Kingdom oldKingdom = user.getKingdom();
        user.setKingdom(null);

        kdc.getMessageManager().send(sender, "cmdLeaveSuccess", oldKingdom.getName());

        for ( Player member : kdc.getOnlinePlayers() ) {
            if ( kdc.getUser(member).getKingdom() != oldKingdom ) continue;
            kdc.getMessageManager().send(member, "cmdLeaveSuccessMembers", user.getName());
        }

        // TODO teleport to spawn
    }
}
