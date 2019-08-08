package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
public class FlagCommand extends CommandBase {

	private final KingdomCraft plugin;

	public FlagCommand(KingdomCraft plugin) {
		super("flag", "kingdom.flag", true, "<kingdom> <flag> <value>");
		this.plugin = plugin;
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 1 ) {
			List<String> kingdoms = new ArrayList<>();
			for ( Kingdom kd : plugin.getApi().getKingdomHandler().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[0].toLowerCase()) ) {
					kingdoms.add(kd.getName());
				}
			}
			return kingdoms;
		}

		if ( args.length == 2 ) {
			List<String> results = new ArrayList<>();
			for (KingdomFlag flag : plugin.getApi().getFlagHandler().getAllFlags()) {
				results.add(flag.getName());
			}
			return results;
		}

		if ( args.length == 3 ) {
			String flagname = args[0];
			KingdomFlag flag = plugin.getApi().getFlagHandler().getFlag(flagname);
			if ( flag == null ) return null;

			List<String> results = new ArrayList<>();

			if ( flag.getType() == Boolean.class ) {
				results.addAll(Arrays.asList("true", "false"));
			} else if ( flag.getType().isEnum() ) {
				for ( Object obj : flag.getType().getEnumConstants() ) {
					results.add(obj.toString());
				}
			}

			return results;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;

		if ( args.length < 2 ) {
			return false;
		}

		if ( args[0].equalsIgnoreCase("list") ) {
			if ( args.length != 2 ) {
				return false;
			}

			Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[1]);
			if ( kingdom == null ) {
				plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[1]);
				return true;
			}

			List<KingdomFlag> flags = plugin.getApi().getFlagHandler().getFlags(kingdom);
			if ( flags.isEmpty()) {
				plugin.getMsg().send(p, "cmdFlagList", "/");
			} else {
				String s = "";
				for ( KingdomFlag flag : flags ) {
					s += ", " + ChatColor.DARK_GRAY + flag.getName() + " " + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', plugin.getApi().getFlagHandler().getFlag(kingdom, flag) + "") + ChatColor.DARK_GRAY + ")";
				}
				s = s.substring(2);
				plugin.getMsg().send(p, "cmdFlagList", s);
			}

			return true;
		}

		Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[0]);
		if ( kingdom == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return true;
		}

		KingdomFlag flag = plugin.getApi().getFlagHandler().getFlag(args[1]);
		if ( flag == null ) {

			String s = "";
			for ( KingdomFlag kf : plugin.getApi().getFlagHandler().getAllFlags() ) {
				s += ", " + kf.getName();
			}
			s = s.substring(2);

			plugin.getMsg().send(sender, "cmdFlagNotExist", args[1], s);
			return true;
		}

		String value = null;
		if ( args.length > 2 ) {
			value = "";
			for (int i = 2; i < args.length; i++) {
				value += " " + args[i];
			}
			value = value.substring(1);
		}

		Object realValue = null;
		if ( value != null ) {
			try {
				realValue = flag.parse(value);
			} catch (ClassCastException ex) {
				plugin.getMsg().send(sender, "cmdFlagInvalidValue", value, flag.getType().getSimpleName());
				return true;
			}
		}

		if ( realValue != null ) {
			plugin.getApi().getFlagHandler().setFlag(kingdom, flag, realValue);
			plugin.getMsg().send(sender, "cmdFlagSet", flag.getName(), kingdom.getName(), value);
		} else {
			plugin.getMsg().send(sender, "cmdFlagRemove", flag.getName(), kingdom.getName());
			plugin.getApi().getFlagHandler().setFlag(kingdom, flag, null);
		}
		plugin.getApi().getKingdomHandler().save(kingdom);

		return true;
	}
}
