/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.bukkit;

import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.bukkit.chat.ChatHandler;
import com.guflan.kingdomcraft.bukkit.command.CommandHandler;
import com.guflan.kingdomcraft.bukkit.config.BukkitConfig;
import com.guflan.kingdomcraft.bukkit.listeners.*;
import com.guflan.kingdomcraft.bukkit.messages.MessageManagerImpl;
import com.guflan.kingdomcraft.bukkit.placeholders.PlaceholderReplacer;
import com.guflan.kingdomcraft.bukkit.scheduler.BukkitScheduler;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.KingdomCraftPlugin;
import com.guflan.kingdomcraft.common.config.KingdomCraftConfig;
import com.guflan.kingdomcraft.common.ebean.StorageContext;
import com.guflan.kingdomcraft.common.scheduler.AbstractScheduler;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class KingdomCraftBukkitPlugin extends JavaPlugin implements KingdomCraftPlugin {

	private KingdomCraftImpl kdc;

	private final BukkitScheduler scheduler;

	public KingdomCraftBukkitPlugin() {
		this.scheduler = new BukkitScheduler(this);
	}

	@Override
	public void onEnable() {
		// LOAD CONFIG

		YamlConfiguration config = new YamlConfiguration();

		File configFile = new File(this.getDataFolder(), "config.yml");
		if ( !configFile.exists() ) {
			saveResource("config.yml", true);
		}

		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getLogger().warning("Database section not found in config.yml!");
			disable();
			return;
		}

		// DATABASE

		if ( !config.contains("database") ) {
			getLogger().warning("Database section not found in config.yml!");
			disable();
			return;
		}

		// For some reason, required ebean classes are not in the classpath of the current classloader.
		// Fix this by changing the class loader to the one of the current class.
		ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		// create database
		ConfigurationSection dbConfig = config.getConfigurationSection("database");
		StorageContext context = new StorageContext(this);
		boolean result = context.init(
				dbConfig.getString("url"),
				dbConfig.getString("driver"),
				dbConfig.getString("username"),
				dbConfig.getString("password"));

		// revert class loader to original
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);

		if ( !result ) {
			log("Error occured during database initialization. Shutting down plugin.", Level.SEVERE);
			disable();
			return;
		}

		// initialize handler
		KingdomCraftConfig cfg = new BukkitConfig(config);
		MessageManager messageManager = new MessageManagerImpl(this);
		this.kdc = new KingdomCraftImpl(this, cfg, context, messageManager);

		// placeholders
		new PlaceholderReplacer(this);

		// chat
		new ChatHandler(this);

		// commands
		CommandHandler commandHandler = new CommandHandler(this);
		PluginCommand command = getCommand("kingdomcraft");
		command.setExecutor(commandHandler);
		command.setTabCompleter(commandHandler);

		// listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ConnectionListener(this), this);
		pm.registerEvents(new FriendlyFireListener(this), this);
		pm.registerEvents(new JoinQuitListener(this), this);
		pm.registerEvents(new RespawnListener(this), this);
		pm.registerEvents(new DeathListener(this), this);

		// kingdom events
		new KingdomJoinQuitListener(this);
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	public KingdomCraftImpl getKdc() {
		return kdc;
	}

	//

	@Override
	public AbstractScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public void log(String msg) {
		getLogger().info(msg);
	}

	@Override
	public void log(String msg, Level level) {
		getLogger().log(level, msg);
	}
}
