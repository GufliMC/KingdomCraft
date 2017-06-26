package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomRelation;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Copyrighted 2017 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class NeutralCommand extends CommandBase {

	public NeutralCommand() {
		super("neutral", "kingdom.neutral", true);
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		if ( args.length == 2 ) {
			ArrayList<String> kingdoms = new ArrayList<>();
			for ( KingdomObject kd : KingdomCraft.getApi().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[1].toLowerCase()) ) kingdoms.add(kd.getName());
			}
			return kingdoms;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length != 1) {
			KingdomCraft.getMsg().send(sender, ("cmdDefaultUsage"));
			return false;
		}
		if ( KingdomCraft.getApi().getUser(p).getKingdom() == null ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultSenderNoKingdom");
			return false;
		}
		if ( KingdomCraft.getApi().getKingdom(args[0]) == null ) {
			KingdomCraft.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return false;
		}
		
		KingdomObject senderkd = KingdomCraft.getApi().getUser(p).getKingdom();
		KingdomObject targetkd = KingdomCraft.getApi().getKingdom(args[0]);

		if ( KingdomCraft.getApi().getRelation(senderkd, targetkd) == KingdomRelation.NEUTRAL ) {
			KingdomCraft.getMsg().send(sender, "cmdNeutralAlready", targetkd.getName());
			return false;
		}

		KingdomCraft.getApi().setRelation(senderkd, targetkd, KingdomRelation.NEUTRAL);

		KingdomCraft.getMsg().send(sender, "cmdNeutralSuccess", targetkd.getName());

		return false;
	}

}
