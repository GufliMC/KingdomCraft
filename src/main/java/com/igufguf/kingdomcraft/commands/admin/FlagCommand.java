package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
public class FlagCommand extends CommandBase {

	private final KingdomCraft plugin;

	public FlagCommand(KingdomCraft plugin) {
		super("flag", "kingdom.flag", true);
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;

		if ( args.length < 2 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}

		if ( args[0].equalsIgnoreCase("list") ) {
			if ( args.length != 2 ) {
				plugin.getMsg().send(sender, "cmdDefaultUsage");
				return false;
			}

			Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[1]);
			if ( kingdom == null ) {
				plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[1]);
				return false;
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

			return false;
		}

		Kingdom kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[0]);
		if ( kingdom == null ) {
			plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
			return false;
		}

		KingdomFlag flag = plugin.getApi().getFlagHandler().getFlag(args[1]);
		if ( flag == null ) {

			String s = "";
			for ( KingdomFlag kf : plugin.getApi().getFlagHandler().getAllFlags() ) {
				s += ", " + kf.getName();
			}
			s = s.substring(2);

			plugin.getMsg().send(sender, "cmdFlagNotExist", args[1], s);
			return false;
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
				return false;
			}
		}

		if ( realValue != null ) {
			plugin.getApi().getFlagHandler().setFlag(kingdom, flag, realValue);
			plugin.getMsg().send(sender, "cmdFlagSet", flag.getName(), kingdom.getName(), value);
		} else {
			plugin.getMsg().send(sender, "cmdFlagRemove", flag.getName(), kingdom.getName());
			plugin.getApi().getFlagHandler().setFlag(kingdom, flag, null);
		}

		return false;
	}
}
