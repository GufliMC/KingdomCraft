package com.igufguf.kingdomcraft.commands.editor;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import com.igufguf.kingdomcraft.handlers.SimpleKingdomHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
		super("delete", "kingdom.delete", true, "<kingdom>");
		this.plugin = plugin;
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;

		if ( args.length < 1 ) {
			return false;
		}

		if ( !(plugin.getApi().getKingdomHandler() instanceof  SimpleKingdomHandler) ) {
			return true;
		}

		SimpleKingdomHandler kh = (SimpleKingdomHandler) plugin.getApi().getKingdomHandler();

		String kdname = args[0].toLowerCase();

		Kingdom kd = kh.getKingdom(kdname);
		if ( kd == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", kdname);
			return true;
		}

		KingdomUser ku = plugin.getApi().getUserHandler().getUser(p);
		if ( !kd.getName().equals(ku.getKingdom()) && !p.hasPermission("kingdom.delete.other")) {
			plugin.getMsg().send(sender, "noPermission");
			return true;
		}

		FileConfiguration fc = kh.getConfiguration();
		fc.getConfigurationSection("kingdoms").set(kd.getName(), null);
		kh.saveConfiguration();

		kh.getKingdoms().remove(kd);

		for ( KingdomUser user : plugin.getApi().getUserHandler().getAllUsers() ) {
			if ( user.getKingdom().equals(kd.getName()) ) {
				plugin.getApi().getUserHandler().setKingdom(user, null);
			}
		}

		plugin.getMsg().send(p, "cmdDeleteSuccess", kd.getName());
		return true;
	}
}
