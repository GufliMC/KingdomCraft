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
import com.guflan.kingdomcraft.bukkit.listeners.ChatListener;
import com.guflan.kingdomcraft.bukkit.command.CommandHandler;
import com.guflan.kingdomcraft.bukkit.config.BukkitConfiguration;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.guflan.kingdomcraft.bukkit.gui.InventoryListener;
import com.guflan.kingdomcraft.bukkit.listeners.*;
import com.guflan.kingdomcraft.bukkit.messages.MessageManagerImpl;
import com.guflan.kingdomcraft.bukkit.placeholders.PlaceholderReplacer;
import com.guflan.kingdomcraft.bukkit.scheduler.BukkitScheduler;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.KingdomCraftPlugin;
import com.guflan.kingdomcraft.common.config.Configuration;
import com.guflan.kingdomcraft.common.ebean.StorageContext;
import com.guflan.kingdomcraft.common.scheduler.AbstractScheduler;
import io.ebean.migration.MigrationException;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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

	public KingdomCraftImpl getKdc() {
		return kdc;
	}

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

	//

	@Override
	public void onEnable() {
		// LOAD CONFIG

		ConfigurationSection config = initConfig("config.yml");
		if ( config == null || !config.contains("database") ) {
			getLogger().warning("Database section not found in config.yml!");
			disable();
			return;
		}

		// For some reason, required ebean classes are not in the classpath of the current classloader.
		// Fix this by changing the class loader to the one of the current class.
		ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		// load database
		StorageContext context;
		try {
			context = new StorageContext(this);

			ConfigurationSection dbConfig = config.getConfigurationSection("database");
			context.init(
				dbConfig.getString("url"),
				dbConfig.getString("driver"),
				dbConfig.getString("username"),
				dbConfig.getString("password")
			);
		} catch (MigrationException ex) {
			if ( ex.getCause() != null ) {
				log(ex.getCause().getMessage(), Level.SEVERE);
			} else {
				log(ex.getMessage(), Level.SEVERE);
			}

			log("Error occured during database initialization. Shutting down plugin.", Level.SEVERE);
			disable();
			return;
		}

		// revert class loader to original
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);

		// initialize handler
		Configuration pluginConfig = new BukkitConfiguration(config.getConfigurationSection("settings"));
		Configuration chatConfig = new BukkitConfiguration(initConfig("chat.yml"));
		Configuration groupsConfig = new BukkitConfiguration(initConfig("groups.yml"));

		MessageManager messageManager = new MessageManagerImpl(this);
		this.kdc = new KingdomCraftImpl(this, context, messageManager, pluginConfig, chatConfig, groupsConfig);

		for ( Player p : Bukkit.getOnlinePlayers() ) {
			this.kdc.onJoin(new BukkitPlayer(p));
		}

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
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new PermissionsListener(this), this);
		pm.registerEvents(new MoveListener(this), this);

		// chat
		if ( chatConfig.contains("enabled") && chatConfig.getBoolean("enabled") ) {
			pm.registerEvents(new ChatListener(this), this);
		}

		// kingdom events
		new KingdomJoinQuitListener(this);

		// placeholders
		new PlaceholderReplacer(this);
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	private ConfigurationSection initConfig(String name) {
		File configFile = new File(this.getDataFolder(), name);
		if ( !configFile.exists() ) {
			saveResource(name, true);
		}

		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			log(e.getMessage(), Level.WARNING);
			return null;
		}

		return config;
	}

}
