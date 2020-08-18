package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.command.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.common.command.DefaultCommandBase;
import org.bukkit.ChatColor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends DefaultCommandBase {

    public ListCommand(KingdomCraftPlugin plugin) {
        super(plugin, "list", 0);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.list") ) {
            plugin.getMessageManager().send(sender, "noPermission");
        }

        List<String> kingdoms = plugin.getKingdomManager().getKingdoms().stream()
                .sorted(Comparator.comparing(Kingdom::isInviteOnly))
                .map(k -> (k.isInviteOnly() ? ChatColor.RED : ChatColor.GREEN) + k.getName())
                .collect(Collectors.toList());

        // TODO add prefix
        sender.sendMessage(plugin.getMessageManager().getMessage("cmdList") + " "
                + String.join(ChatColor.GRAY + ", ", kingdoms));
    }
}
