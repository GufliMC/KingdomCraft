package com.igufguf.kingdomcraft;

import com.igufguf.kingdomcraft.commands.CommandHandler;
import com.igufguf.kingdomcraft.commands.executors.*;
import com.igufguf.kingdomcraft.listeners.CommandListener;
import com.igufguf.kingdomcraft.objects.KingdomData;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import com.igufguf.kingdomcraft.listeners.*;
import com.igufguf.kingdomcraft.commands.executors.ListCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.Arrays;

/**
 * Copyrighted 2017 iGufGuf
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

	public static String prefix = ChatColor.RED + ChatColor.BOLD.toString() + "KingdomCraft" + ChatColor.DARK_GRAY + ChatColor.BOLD + " > " + ChatColor.GRAY;

	private static com.igufguf.kingdomcraft.KingdomCraft plugin;

	private static KingdomCraftApi api;
	private static KingdomCraftConfig config;
	private static KingdomCraftMessages messages;
	private static ChatListener chat;
	
	public void onEnable() {
		plugin = this;

		//load defaults
		config = new KingdomCraftConfig(this);
		api = new KingdomCraftApi(this);
		messages = new KingdomCraftMessages(this);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ConnectionListener(), this);
		pm.registerEvents(chat = new ChatListener(this), this);
		pm.registerEvents(new MoveListener(), this);
		pm.registerEvents(new DamageListener(), this);
		pm.registerEvents(new RespawnListener(), this);
		pm.registerEvents(new CommandListener(), this);

		//loading the commands
		PluginCommand cmd = getCommand("kingdom");
		cmd.setAliases(Arrays.asList("k", "kd", "kingdoms", "kingdomcraft", "kdc"));
		
		CommandHandler cmdhandler = new CommandHandler();
		cmd.setExecutor(cmdhandler);
		cmd.setTabCompleter(cmdhandler);
		
		loadCommands();

		for ( Player p : Bukkit.getOnlinePlayers() ) {
			KingdomUser user = KingdomCraft.getApi().getOfflineUser(p.getUniqueId());
			KingdomCraft.getApi().registerUser(user);
		}

		new BukkitRunnable() {
			public void run() {
				save();
			}
		}.runTaskTimer(this, 60 * 20L, 60 * 20L);
	}
	
	public void onDisable() {
		save();
	}
	
	private void loadCommands() {
		new InfoCommand();
		new InviteCommand();
		new JoinCommand();
		new KickCommand();
		new LeaveCommand();
		new ListCommand();
		new ReloadCommand();
		new SetrankCommand();
		new SetspawnCommand();
		new SetstatusCommand();
		new SpawnCommand();
		new FriendlyCommand();
		new EnemyCommand();
		new NeutralCommand();
		new ChannelCommand();
		new HelpCommand();
		new SetCommand();
		new SocialSpyCommand();
	}

	public void save() {
		for ( KingdomObject kd : api.getKingdoms() ) {
			try {
				File kingdomfile = new File(getDataFolder(), "/kingdoms/" + kd.getName() + ".yml");
				FileConfiguration kingdomcfg = YamlConfiguration.loadConfiguration(kingdomfile);
				kd.save(kingdomcfg);

				kingdomcfg.save(kingdomfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File usersfile = new File(getDataFolder(), "users.yml");
		FileConfiguration userscfg = YamlConfiguration.loadConfiguration(usersfile);
		for ( KingdomUser user : api.getUsers() ) {
			user.save(userscfg);
		}

		try {
			userscfg.save(usersfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save(KingdomData object) {
		if ( object instanceof KingdomObject ) {
			KingdomObject kd = (KingdomObject) object;

			File kingdomfile = new File(getDataFolder(), "/kingdoms/" + kd.getName() + ".yml");
			FileConfiguration kingdomcfg = YamlConfiguration.loadConfiguration(kingdomfile);
			kd.save(kingdomcfg);

			try {
				kingdomcfg.save(kingdomfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ( object instanceof KingdomUser ) {
			KingdomUser user = (KingdomUser) object;

			File usersfile = new File(getDataFolder(), "users.yml");
			FileConfiguration userscfg = YamlConfiguration.loadConfiguration(usersfile);

			user.save(userscfg);

			try {
				userscfg.save(usersfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static KingdomCraftApi getApi() {
		return api;
	}

	public static KingdomCraftConfig getConfg() {
		return config;
	}

	public static ChatListener getChat() {
		return chat;
	}

	public static com.igufguf.kingdomcraft.KingdomCraft getPlugin() {
		return plugin;
	}

	public static KingdomCraftMessages getMsg() {
		return messages;
	}

}
