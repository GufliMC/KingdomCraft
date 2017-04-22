package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomUser;
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
public class InviteCommand extends CommandBase {

	public InviteCommand() {
		super("invite", "kingdom.invite", true);
		
		CommandHandler.register(this);
	}
	
	@Override
	public ArrayList<String> tabcomplete(String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		
		if ( args.length != 1 ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultUsage"));
			return false;
		}
		if ( KingdomCraft.getApi().getUser(p).getKingdom() == null ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultSenderNoKingdom"));
			return false;
		}
		
		String username = args[0];
		KingdomUser user = KingdomCraft.getApi().getUser(username);
		
		if ( user == null ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultNoPlayer", username));
			return false;
		}

		if ( user.getKingdom() == KingdomCraft.getApi().getUser(p).getKingdom() ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdInviteAlready", user.getName()));
			return false;
		}


		String kingdom = KingdomCraft.getApi().getUser(p).getKingdom().getName();
		if ( user.hasInList("invites", kingdom) ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdInviteAlready", user.getName()));
			return false;
		}

		user.addInList("invites", kingdom);

		if ( user.getPlayer() != null ) {
			user.getPlayer().sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdInviteTarget", kingdom));
		} else {
			KingdomCraft.getPlugin().save(user);
		}

		sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdInviteSender", p.getName(), kingdom));

		return false;
	}
	
}
