package com.igufguf.kingdomcraft;

import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.commands.executors.*;
import com.igufguf.kingdomcraft.listeners.*;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

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
public class KingdomCraft extends JavaPlugin {

	private String prefix = ChatColor.RED + ChatColor.BOLD.toString() + "KingdomCraft" + ChatColor.DARK_GRAY + ChatColor.BOLD + " > " + ChatColor.GRAY;

	private KingdomCraft plugin;

	private KingdomCraftApi api;
	private KingdomCraftConfig config;
	private KingdomCraftMessages messages;

	private CommandHandler cmdHandler;

	public void onEnable() {
		plugin = this;

		// create data folder
		if ( !getDataFolder().exists() ) {
			getDataFolder().mkdirs();
		}

		//load defaults
		try {
			config = new KingdomCraftConfig(this);
			messages = new KingdomCraftMessages(this);
			api = new KingdomCraftApi(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ConnectionListener(this), this);
		pm.registerEvents(new MoveListener(this), this);
		pm.registerEvents(new DamageListener(this), this);
		pm.registerEvents(new RespawnListener(this), this);
		pm.registerEvents(new CommandListener(this), this);

		if ( api.getChatManager().isChatSystemEnabled() )
			pm.registerEvents(new ChatListener(this), this);

		//loading the commands
		this.cmdHandler = new CommandHandler(this);

		PluginCommand cmd = getCommand("kingdom");
		cmd.setExecutor(cmdHandler);
		cmd.setTabCompleter(cmdHandler);
		
		loadCommands();

		for ( Player p : Bukkit.getOnlinePlayers() ) {
			KingdomUser user = api.getUserManager().getOfflineUser(p.getUniqueId());
			api.getUserManager().registerUser(user);
		}

		new BukkitRunnable() {
			public void run() {
				save();
			}
		}.runTaskTimer(this, 60 * 20L, 60 * 20L); // every minute

		getLogger().info("Enabled " + this.getDescription().getFullName());
	}
	
	public void onDisable() {
		save();
	}
	
	private void loadCommands() {
		new InfoCommand(this);
		new InviteCommand(this);
		new JoinCommand(this);
		new KickCommand(this);
		new LeaveCommand(this);
		new ListCommand(this);
		new ReloadCommand(this);
		new SetRankCommand(this);
		new SetSpawnCommand(this);
		new SetStatusCommand(this);
		new SpawnCommand(this);
		new FriendlyCommand(this);
		new EnemyCommand(this);
		new NeutralCommand(this);
		new ChannelCommand(this);
		new HelpCommand(this);
		new SetCommand(this);
		new SocialSpyCommand(this);
	}

	public void save() {
		for ( KingdomObject ko : api.getKingdomManager().getKingdoms() ) {
			api.getKingdomManager().saveKingdom(ko);
		}

		for ( KingdomUser user : api.getUserManager().getUsers() ) {
			api.getUserManager().save(user);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public KingdomCraftApi getApi() {
		return api;
	}

	public KingdomCraftConfig getCfg() {
		return config;
	}

	public KingdomCraft getPlugin() {
		return plugin;
	}

	public KingdomCraftMessages getMsg() {
		return messages;
	}

	public CommandHandler getCmdHandler() {
		return cmdHandler;
	}
}
