package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;
import org.bukkit.ChatColor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends DefaultCommandBase {

    public ListCommand(KingdomCraft kdc) {
        super(kdc, "list", 0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.list") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        List<String> kingdoms = kdc.getKingdoms().stream()
                .sorted(Comparator.comparing(Kingdom::isInviteOnly))
                .map(k -> (k.isInviteOnly() ? ChatColor.RED : ChatColor.GREEN) + k.getName())
                .collect(Collectors.toList());

        kdc.getMessageManager().send(sender, "cmdList", String.join(ChatColor.GRAY + ", ", kingdoms));
    }
}
