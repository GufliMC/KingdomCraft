/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.bukkit;

import com.gufli.kingdomcraft.bukkit.command.CommandHandler;
import com.gufli.kingdomcraft.bukkit.config.BukkitConfiguration;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryListener;
import com.gufli.kingdomcraft.bukkit.listeners.*;
import com.gufli.kingdomcraft.bukkit.messages.MessageManager;
import com.gufli.kingdomcraft.bukkit.permissions.VaultPermissionHandler;
import com.gufli.kingdomcraft.bukkit.placeholders.PlaceholderReplacer;
import com.gufli.kingdomcraft.bukkit.placeholders.VaultPlaceholderReplacer;
import com.gufli.kingdomcraft.bukkit.scheduler.BukkitScheduler;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.KingdomCraftPlugin;
import com.gufli.kingdomcraft.common.config.Configuration;
import com.gufli.kingdomcraft.common.ebean.StorageContext;
import com.gufli.kingdomcraft.common.scheduler.AbstractScheduler;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.impl.SimpleLogger;

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

	@Override
	public String colorify(String msg) {
		return ChatColor.translateAlternateColorCodes('&', StringEscapeUtils.unescapeJava(msg));
	}

	@Override
	public String decolorify(String msg) {
		return ChatColor.stripColor(msg);
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
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, config.getBoolean("debug") ? "TRACE" : "WARN");
		StorageContext context = new StorageContext(this);
		ConfigurationSection dbConfig = config.getConfigurationSection("database");
		context.init(
			dbConfig.getString("url"),
			dbConfig.getString("driver"),
			dbConfig.getString("username"),
			dbConfig.getString("password")
		);

		if ( !context.isInitialized() ) {
			log("Error occured during database initialization. Shutting down plugin.", Level.SEVERE);
			disable();
			return;
		}

		// revert class loader to original
		Thread.currentThread().setContextClassLoader(originalContextClassLoader);

		// initialize handler
		Configuration pluginConfig = new BukkitConfiguration(config.getConfigurationSection("settings"));
		Configuration chatConfig = new BukkitConfiguration(initConfig("chat.yml"));
		Configuration groupsConfig = new BukkitConfiguration(initConfig("permissions.yml"));

		this.kdc = new KingdomCraftImpl(this, context, pluginConfig, chatConfig, groupsConfig);

		for ( Player p : Bukkit.getOnlinePlayers() ) {
			this.kdc.onLoad(new BukkitPlayer(p));
		}

		// initialize messages
		new MessageManager(this);

		// commands
		CommandHandler commandHandler = new CommandHandler(this);
		PluginCommand command = getCommand("kingdomcraft");
		command.setExecutor(commandHandler);
		command.setTabCompleter(commandHandler);

		// listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new FriendlyFireListener(this), this);
		pm.registerEvents(new JoinQuitListener(this), this);
		pm.registerEvents(new RespawnListener(this), this);
		pm.registerEvents(new DeathListener(this), this);
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new PermissionsListener(this), this);
		pm.registerEvents(new MoveListener(this), this);

		ConnectionListener cl = new ConnectionListener(this);
		pm.registerEvents(cl, this);
		kdc.getEventManager().addListener(cl);

		// chat
		if ( chatConfig.contains("enabled") && chatConfig.getBoolean("enabled") ) {
			pm.registerEvents(new ChatListener(this), this);
		}

		// vault permissions hook
		new VaultPermissionHandler(this);

		// kingdom events
		new KingdomJoinQuitListener(this);

		// placeholders
		new PlaceholderReplacer(this);
		new VaultPlaceholderReplacer(this);

		getLogger().info("Enabled " + this.getDescription().getFullName());
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	@Override
	public void reload() {
		ConfigurationSection config = initConfig("config.yml");
		if ( config == null ) {
			log("Invalid configuration, cannot reload Kingdomcraft!", Level.WARNING);
			return;
		}

		Configuration pluginConfig = new BukkitConfiguration(config.getConfigurationSection("settings"));
		kdc.getConfig().reload(pluginConfig);

		Configuration groupsConfig = new BukkitConfiguration(initConfig("permissions.yml"));
		kdc.getPermissionManager().reload(groupsConfig);

		Configuration chatConfig = new BukkitConfiguration(initConfig("chat.yml"));
		kdc.getChatManagerImpl().reload(chatConfig);

		kdc.getEventDispatcher().dispatchReload();
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
