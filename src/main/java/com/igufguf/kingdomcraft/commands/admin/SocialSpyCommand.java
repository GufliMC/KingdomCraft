package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 0 ) {
			return false;
		}

		KingdomUser user = plugin.getApi().getUserHandler().getUser((Player) sender);

		boolean result = user.isSocialSpyEnabled();

		if ( result ) {
			user.setSocialSpyEnabled(false);
			plugin.getMsg().send(sender, "cmdSocialspyDisable");
		} else {
			user.setSocialSpyEnabled(true);
			plugin.getMsg().send(sender, "cmdSocialspyEnable");
		}
		
		return true;
	}

}
