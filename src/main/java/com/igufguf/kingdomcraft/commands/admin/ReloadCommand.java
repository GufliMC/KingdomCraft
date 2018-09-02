package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.events.KingdomReloadEvent;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

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
public class ReloadCommand extends CommandBase {

	private final KingdomCraft plugin;

	public ReloadCommand(KingdomCraft plugin) {
		super("reload", "kingdom.reload", false);

		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if ( args.length != 0 ) {
			plugin.getMsg().send(sender, "cmdDefaultUsage");
			return false;
		}
		
		sender.sendMessage(plugin.getPrefix() + "Reloading kingdomcraft...");

		PluginManager pm = plugin.getServer().getPluginManager();

		pm.disablePlugin(plugin);
		pm.enablePlugin(plugin);

		Bukkit.getServer().getPluginManager().callEvent(new KingdomReloadEvent());

		return false;
	}

}
