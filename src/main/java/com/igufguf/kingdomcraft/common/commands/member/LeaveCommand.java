package com.igufguf.kingdomcraft.common.commands.member;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Kingdom;
import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.common.commands.DefaultCommandBase;

public class LeaveCommand extends DefaultCommandBase {

    public LeaveCommand(KingdomCraftPlugin plugin) {
        super(plugin, "leave", 0, true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = sender.getPlayer();
        if (player.getKingdom() == null) {
            plugin.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Kingdom oldKingdom = player.getKingdom();
        plugin.getPlayerManager().leaveKingdom(player);

        plugin.getMessageManager().send(sender, "cmdLeaveSuccess", oldKingdom.getName());

        for (Player member : plugin.getKingdomManager().getOnlineMembers(oldKingdom)) {
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
