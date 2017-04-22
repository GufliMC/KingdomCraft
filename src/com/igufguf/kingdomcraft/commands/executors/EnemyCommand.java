package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRelation;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
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
public class EnemyCommand extends CommandBase {

	public EnemyCommand() {
		super("enemy", "kingdom.enemy", true);
		
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
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultUsage"));
			return false;
		}
		if ( KingdomCraft.getApi().getUser(p).getKingdom() == null ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultSenderNoKingdom"));
			return false;
		}
		if ( KingdomCraft.getApi().getKingdom(args[0]) == null ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdDefaultKingdomNotExist", args[0]));
			return false;
		}
		
		KingdomObject senderkd = KingdomCraft.getApi().getUser(p).getKingdom();
		KingdomObject targetkd = KingdomCraft.getApi().getKingdom(args[0]);

		if ( KingdomCraft.getApi().getRelation(senderkd, targetkd) == KingdomRelation.ENEMY ) {
			sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdEnemyAlready", targetkd.getName()));
			return false;
		}

		KingdomCraft.getApi().setRelation(senderkd, targetkd, KingdomRelation.ENEMY);
		KingdomCraft.getApi().setRelation(targetkd, senderkd, KingdomRelation.ENEMY);

		sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdEnemySuccess", targetkd.getName()));

		for ( Player on : Bukkit.getOnlinePlayers() ) {
			KingdomUser ku = KingdomCraft.getApi().getUser(on);
			if ( ku.getKingdom() == senderkd && on != p) {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdEnemySuccessMembers", targetkd.getName()));
			} else if ( ku.getKingdom() == targetkd ) {
				sender.sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdEnemySuccessTarget", senderkd.getName()));
			}
		}

		return false;
	}

}
