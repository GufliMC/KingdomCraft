package com.igufguf.kingdomcraft.commands.member;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.domain.Kingdom;
import com.igufguf.kingdomcraft.domain.Player;
import com.igufguf.kingdomcraft.models.CommandBase;
import org.bukkit.command.CommandSender;

/**
 * Copyrighted 2020 iGufGuf
 * <p>
 * This file is part of KingdomCraft.
 * <p>
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 **/
public class LeaveCommand extends CommandBase {

    public LeaveCommand(KingdomCraft kingdomCraft) {
        super(kingdomCraft, "leave");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sendInvalidUsage(sender);
            return;
        }


        Player player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);
        if (player.getKingdom() == null) {
            kingdomCraft.messageHandler.send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Kingdom oldKingdom = player.getKingdom();

        player.setKingdom(null);
        player.save();

        kingdomCraft.messageHandler.send(sender, "cmdLeaveSuccess", oldKingdom.getName());

        for (Player member : kingdomCraft.kingdomHandler.getOnlineMembers(oldKingdom)) {
            kingdomCraft.messageHandler.send(member, "cmdLeaveSuccessMembers", player.getName());
        }

        // TODO teleport to spawn
		/*
		if ( plugin.getCfg().has("spawn-on-kingdom-leave") && plugin.getCfg().getBoolean("spawn-on-kingdom-leave") ) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn " + p.getName());
		}
		*/
    }

}
