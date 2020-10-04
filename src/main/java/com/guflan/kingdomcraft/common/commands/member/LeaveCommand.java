package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;

public class LeaveCommand extends DefaultCommandBase {

    public LeaveCommand(KingdomCraftPlugin plugin) {
        super(plugin, "leave", 0, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.leave") ) {
            plugin.getMessageManager().send(sender, "noPermission");
        }

        User player = sender.getPlayer();
        if (player.getKingdom() == null) {
            plugin.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Kingdom oldKingdom = player.getKingdom();
        plugin.getPlayerManager().leaveKingdom(player);

        plugin.getMessageManager().send(sender, "cmdLeaveSuccess", oldKingdom.getName());

        for ( Player member : plugin.getKingdomManager().getOnlineMembers(oldKingdom) ) {
            plugin.getMessageManager().send(member, "cmdLeaveSuccessMembers", player.getName());
        }

        // TODO teleport to spawn
		/*
		if ( plugin.getCfg().has("spawn-on-kingdom-leave") && plugin.getCfg().getBoolean("spawn-on-kingdom-leave") ) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn " + p.getName());
		}
		*/
    }
}
