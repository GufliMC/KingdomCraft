package com.guflan.kingdomcraft.common.commands.management;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class DeleteCommand extends DefaultCommandBase {

    public DeleteCommand(KingdomCraft kdc) {
        super(kdc, "delete", 1);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.delete") && !sender.hasPermission("kingdom.delete.other")) {
            kdc.getMessageManager().send(sender, "noPermission");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        if ( sender instanceof Player) {
            User user = kdc.getUser((Player) sender);

            if ( user.getKingdom() != kingdom && !sender.hasPermission("kingdom.delete.other")) {
                kdc.getMessageManager().send(sender, "noPermission");
                return;
            }
        }

        for ( Player p : kdc.getOnlinePlayers() ) {
            if ( p.equals(sender) || kdc.getUser(p).getKingdom() != kingdom ) continue;
            kdc.getMessageManager().send(p, "cmdDeleteSuccessMembers");
        }

        kdc.delete(kingdom);
        kdc.getMessageManager().send(sender, "cmdDeleteSuccess", kingdom.getName());
    }
}
