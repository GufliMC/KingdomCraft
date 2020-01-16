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

import java.util.ArrayList;
import java.util.Arrays;
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
public class EditCommand extends CommandBase {

	private final KingdomCraft plugin;

	private final List<String> restrictedOptions = new ArrayList<>(Arrays.asList("ranks"));

	public EditCommand(KingdomCraft plugin) {
		super("edit", "kingdom.edit", true, "<option> <value>");
		this.plugin = plugin;
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;

		if ( !(plugin.getApi().getKingdomHandler() instanceof  SimpleKingdomHandler) ) {
			return true;
		}

		Kingdom kd;
		String option;
		String value;

		if ( args.length == 3 ) {
			if ( !p.hasPermission("kingdom.edit.other") ) {
				plugin.getMsg().send(sender, "noPermission");
				return true;
			}

			kd = plugin.getApi().getKingdomHandler().getKingdom(args[0]);

			option = args[1];
			value = args[2].replace("_", " ");

		} else {
			option = args[0];
			value = args[1].replace("_", " ");

			KingdomUser user = plugin.getApi().getUserHandler().getUser(p);
			kd = plugin.getApi().getKingdomHandler().getKingdom(user.getKingdom());

			if (kd == null) {
				plugin.getMsg().send(p, "cmdDefaultSenderNoKingdom");
				return true;
			}
		}

		option = option.toLowerCase();
		if ( restrictedOptions.contains(option) ) {
			plugin.getMsg().send(p, "cmdEditFailed");
			return true;
		}

		if ( !change(kd, option, value) ) {
			plugin.getMsg().send(p, "cmdEditFailed");
			return true;
		}

		plugin.getMsg().send(p, "cmdEditSuccess", option, ChatColor.translateAlternateColorCodes('&', value), kd.getName());
		return true;
	}

	private boolean change(Kingdom kd, String option, String value) {
		SimpleKingdomHandler kh = (SimpleKingdomHandler) plugin.getApi().getKingdomHandler();

		FileConfiguration fc = kh.getConfiguration();
		ConfigurationSection section = fc.getConfigurationSection("kingdoms").getConfigurationSection(kd.getName());

		section.set(option, value);
		kh.saveConfiguration();

		kh.getKingdoms().remove(kd);
		kh.load(kd.getName());
		return true;
	}
}
