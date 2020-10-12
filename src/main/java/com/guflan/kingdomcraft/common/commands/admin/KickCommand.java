package com.guflan.kingdomcraft.common.commands.admin;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KickCommand extends DefaultCommandBase {

    public KickCommand(KingdomCraft kdc) {
        super(kdc, "kick", 1);
    }

    @Override
    public List<String> autocomplete(CommandSender sender, String[] args) {
        if ( sender.hasPermission("kingdom.kick.other") ) {
            return kdc.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }

        User user = kdc.getUser((Player) sender);
        if ( sender.hasPermission("kingdom.kick") && user.getKingdom() != null ) {
            return kdc.getOnlinePlayers().stream().filter(p -> kdc.getUser(p).getKingdom() == user.getKingdom())
                    .map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.kick") && !sender.hasPermission("kingdom.kick.other")) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if (target == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultNoPlayer", args[0]);
                    return;
                }

                Kingdom kingdom = target.getKingdom();
                if (kingdom == null) {
                    kdc.getMessageManager().send(sender, "cmdDefaultTargetNoKingdom", target.getName());
                    return;
                }

                if ( sender instanceof Player ) {
                    User user = kdc.getUser((Player) sender);

                    // kick other kingdom
                    if (kingdom != user.getKingdom() && !sender.hasPermission("kingdom.kick.other")) {
                        kdc.getMessageManager().send(sender, "noPermissionCmd");
                        return;
                    }

                    // kick own kingdom
                    // TODO hierarchy check
                }

                target.setKingdom(null);
                kdc.save(target);

                Player targetPlayer = kdc.getPlayer(target);
                if ( targetPlayer != null ) {
                    kdc.getMessageManager().send(targetPlayer, "cmdKickTarget", kingdom.getName());
                }

                kdc.getMessageManager().send(sender, "cmdKickSender", target.getName(), kingdom.getName());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
