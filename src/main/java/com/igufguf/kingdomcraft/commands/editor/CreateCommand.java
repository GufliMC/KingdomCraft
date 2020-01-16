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
public class CreateCommand extends CommandBase {

	private final KingdomCraft plugin;

	public CreateCommand(KingdomCraft plugin) {
		super("create", "kingdom.create", true, "<kingdom>");
		this.plugin = plugin;
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;

		if ( args.length != 1 ) {
			return false;
		}

		if ( !(plugin.getApi().getKingdomHandler() instanceof  SimpleKingdomHandler) ) {
			return false;
		}

		if ( plugin.getApi().getUserHandler().getUser(p).getKingdom() != null ) {
			plugin.getMsg().send(sender, "cmdJoinAlready");
			return true;
		}

		SimpleKingdomHandler kh = (SimpleKingdomHandler) plugin.getApi().getKingdomHandler();

		String kdname = args[0].toLowerCase();

		if ( kh.getKingdom(kdname) != null ) {
			plugin.getMsg().send(p, "cmdCreateAlreadyExists", kdname);
			return true;
		}

		if ( !kdname.matches("[a-z]+") ) {
			plugin.getMsg().send(p, "cmdCreateNameInvalid", kdname);
			return true;
		}

		FileConfiguration fc = kh.getConfiguration();
		ConfigurationSection section = fc.getConfigurationSection("kingdoms").createSection(kdname);

		section.set("prefix", "&7[&8" + kdname + "&7]");
		kh.saveConfiguration();

		Kingdom kd = kh.load(kdname);

		KingdomUser user = plugin.getApi().getUserHandler().getUser(p);
		plugin.getApi().getUserHandler().setKingdom(user, kd);
		plugin.getApi().getUserHandler().setRank(user, kd.getLeaderRank());

		plugin.getMsg().send(p, "cmdCreateSuccess", kdname);
		return true;
	}
}
