package com.igufguf.kingdomcraft.commands.executors;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.commands.CommandBase;
import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
public class SocialSpyCommand extends CommandBase {

	private final KingdomCraft plugin;

	public SocialSpyCommand(KingdomCraft plugin) {
		super("socialspy", "kingdom.socialspy", true);
		addAliasses("ss", "spy");

		this.plugin = plugin;

		plugin.getCmdHandler().register(this);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 0 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}

		KingdomUser user = plugin.getApi().getUserManager().getUser((Player) sender);

		boolean result = user.hasData("socialspy") && (boolean) user.getData("socialspy");

		if ( result ) {
			user.setData("socialspy", false);
			plugin.getMsg().send(sender, "cmdSocialspyDisable");
		} else {
			user.setData("socialspy", true);
			plugin.getMsg().send(sender, "cmdSocialspyEnable");
		}
		
		return false;
	}

}
