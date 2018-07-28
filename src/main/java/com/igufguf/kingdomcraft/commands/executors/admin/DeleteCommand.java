package com.igufguf.kingdomcraft.commands.executors.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyrighted 2018 iGufGuf
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
public class DeleteCommand extends CommandBase {

	private final KingdomCraft plugin;

	public DeleteCommand(KingdomCraft plugin) {
		super("delete", "kingdom.delete", false);

		this.plugin = plugin;

		plugin.getCmdHandler().register(this);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {

		if ( args.length != 1 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}

		String name = args[0];
		KingdomObject kingdom = plugin.getApi().getKingdomManager().getKingdom(name);

		if ( kingdom == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", name);
			return false;
		}

		if ( sender instanceof Player) {
			KingdomUser user = plugin.getApi().getUserManager().getUser((Player) sender);

			if ( !user.hasLocalData("kingdom-deletion") ) {
				user.setLocalData("kingdom-deletion", new ArrayList<KingdomDeleteRequest>());
			}

			KingdomDeleteRequest success = null;
			for ( KingdomDeleteRequest req : user.getLocalList("kingdom-deletion", KingdomDeleteRequest.class) ) {
				if ( req.kingdom == kingdom ) {
					success = req;
					break;
				}
			}

			if ( success == null || (System.currentTimeMillis() - success.timestamp) > 15000 ) {
				user.addInLocalList("kingdom-deletion", new KingdomDeleteRequest(kingdom, System.currentTimeMillis()));
				plugin.getMsg().send(sender, "cmdDeleteConfirm", name);
				return false;
			}
		}

		boolean success = plugin.getApi().getKingdomManager().deleteKingdom(name);

		if ( !success ) {
			plugin.getMsg().send(sender, "cmdDeleteFailed", name);
			return false;
		}

		plugin.getMsg().send(sender, "cmdCreateSuccess", name);
		return false;
	}


	private class KingdomDeleteRequest {
		KingdomObject kingdom;
		long timestamp;

		public KingdomDeleteRequest(KingdomObject kingdom, long timestamp) {
			this.kingdom = kingdom;
			this.timestamp = timestamp;
		}
	}

}
