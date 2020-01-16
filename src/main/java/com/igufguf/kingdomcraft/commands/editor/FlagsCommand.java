package com.igufguf.kingdomcraft.commands.editor;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import com.igufguf.kingdomcraft.api.models.flags.KingdomFlag;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
public class FlagsCommand extends CommandBase {

	private final KingdomCraft plugin;

	public FlagsCommand(KingdomCraft plugin) {
		super("flags", "kingdom.flag", true, "<kingdom>");
		this.plugin = plugin;
	}

	@Override
	public List<String> tabcomplete(CommandSender sender, String[] args) {
		if ( args.length == 1 && sender.hasPermission("kingdom.flag.other")) {
			List<String> kingdoms = new ArrayList<>();
			for ( Kingdom kd : plugin.getApi().getKingdomHandler().getKingdoms() ) {
				if ( kd.getName().toLowerCase().startsWith(args[0].toLowerCase()) ) {
					kingdoms.add(kd.getName());
				}
			}
			return kingdoms;
		}
		return null;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player p = (Player) sender;

		if ( args.length > 1 ) {
			return false;
		}

		Kingdom kingdom;
		if ( args.length == 1 ) {
			if ( !p.hasPermission("kingdom.flag.other")) {
				plugin.getMsg().send(sender, "noPermissionCmd");
				return true;
			}

			kingdom = plugin.getApi().getKingdomHandler().getKingdom(args[0]);
			if (kingdom == null) {
				plugin.getMsg().send(sender, "cmdDefaultKingdomNotExist", args[0]);
				return true;
			}
		} else {
			kingdom = plugin.getApi().getKingdomHandler().getKingdom(plugin.getApi().getUserHandler().getUser(p).getKingdom());
			if ( kingdom == null ) {
				plugin.getMsg().send(p, "cmdDefaultSenderNoKingdom");
				return true;
			}
		}

		Map<KingdomFlag, Object> flags = plugin.getApi().getFlagHandler().getFlags(kingdom);
		if ( flags.isEmpty()) {
			plugin.getMsg().send(p, "cmdFlagList", "/");
		} else {
			String s = "";
			for ( KingdomFlag flag : flags.keySet() ) {
				s += ", " + ChatColor.DARK_GRAY + flag.getName() + " " + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', flags.get(flag) + "") + ChatColor.DARK_GRAY + ")";
			}
			s = s.substring(2);
			plugin.getMsg().send(p, "cmdFlagList", s);
		}

		return true;
	}
}
