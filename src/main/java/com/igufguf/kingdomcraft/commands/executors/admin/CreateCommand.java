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
public class CreateCommand extends CommandBase {

	private final KingdomCraft plugin;

	public CreateCommand(KingdomCraft plugin) {
		super("create", "kingdom.create", false);

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
		if ( kingdom != null ) {
			plugin.getMsg().send(sender, "cmdCreateAlreadyExists", name);
			return false;
		}

		if ( !name.matches("[a-zA-Z]+") ) {
			plugin.getMsg().send(sender, "errorInvalidString", name);
			return false;
		}

		kingdom = plugin.getApi().getKingdomManager().createKingdom(name);

		if ( kingdom == null ) {
			plugin.getMsg().send(sender, "cmdCreateFailed", name);
			return false;
		}

		plugin.getMsg().send(sender, "cmdCreateSuccess", name);
		return false;
	}

}
