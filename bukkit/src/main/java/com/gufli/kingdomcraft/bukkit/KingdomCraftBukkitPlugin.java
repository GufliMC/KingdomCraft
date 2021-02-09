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

import com.gufli.kingdomcraft.api.events.PluginReloadEvent;
import com.gufli.kingdomcraft.bukkit.command.CommandHandler;
import com.gufli.kingdomcraft.bukkit.config.BukkitConfiguration;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.gui.InventoryListener;
import com.gufli.kingdomcraft.bukkit.item.BukkitItemSerializer;
import com.gufli.kingdomcraft.bukkit.listeners.*;
import com.gufli.kingdomcraft.bukkit.menu.MenuChatListener;
import com.gufli.kingdomcraft.bukkit.permissions.VaultPermissionHandler;
import com.gufli.kingdomcraft.bukkit.placeholders.PlaceholderReplacer;
import com.gufli.kingdomcraft.bukkit.placeholders.VaultPlaceholderReplacer;
import com.gufli.kingdomcraft.bukkit.scheduler.BukkitScheduler;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.KingdomCraftPlugin;
import com.gufli.kingdomcraft.common.config.Configuration;
import com.gufli.kingdomcraft.common.ebean.StorageContext;
import com.gufli.kingdomcraft.common.scheduler.AbstractScheduler;
import io.avaje.classpath.scanner.Resource;
import io.avaje.classpath.scanner.core.Scanner;
import io.ebean.migration.runner.LocalMigrationResources;
import io.ebeaninternal.server.lib.Str;
import org.apache.commons.io.FileUtils;
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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
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

		// item serializer
		new BukkitItemSerializer();

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

		// load plugin config
		Configuration pluginConfig = new BukkitConfiguration(config.getConfigurationSection("settings"));

		// load chat config
		ConfigurationSection chatConfig = initConfig("chat.yml");
		if ( chatConfig == null ) {
			log("Cannot load chat.yml. Shutting down plugin.", Level.SEVERE);
			disable();
			return;
		}

		// load permissions config
		ConfigurationSection permissionsConfig = initConfig("permissions.yml");
		if ( permissionsConfig == null ) {
			log("Cannot load permissions.yml. Shutting down plugin.", Level.SEVERE);
			disable();
			return;
		}

		this.kdc = new KingdomCraftImpl(this, context);

		// Initialize config files
		this.kdc.getConfig().load(pluginConfig);
		this.kdc.getChatManager().load(new BukkitConfiguration(chatConfig));
		this.kdc.getPermissionManager().load(new BukkitConfiguration(permissionsConfig));

		// Load online players
		for ( Player p : Bukkit.getOnlinePlayers() ) {
			this.kdc.onLoad(new BukkitPlayer(p));
		}

		// initialize messages
		loadMessages(pluginConfig.getString("language"));

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

		// menu chat callback
		pm.registerEvents(new MenuChatListener(this), this);

		// bStats
		Metrics metrics = new Metrics(this, 10101);

		// Number of kingdoms chart
		metrics.addCustomChart(new Metrics.SimplePie("kingdoms", () -> {
			int amount = kdc.getKingdoms().size();
			if ( amount <= 5 ) {
				return "0-5";
			} else if ( amount <= 10 ) {
				return "6-10";
			} else if ( amount <= 15 ) {
				return "11-15";
			} else if ( amount <= 20 ) {
				return "16-20";
			} else if ( amount <= 25 ) {
				return "21-25";
			} else {
				return "25+";
			}
		}));

		getLogger().info("Enabled " + this.getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		kdc.stop();
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	public void reload() {
		ConfigurationSection config = initConfig("config.yml");
		if ( config != null && config.contains("settings") ) {
			config = config.getConfigurationSection("settings");

			kdc.getConfig().load(new BukkitConfiguration(config));
			loadMessages(config.getString("language"));
		} else {
			log("An error occured, cannot reload config.yml", Level.WARNING);
		}

		ConfigurationSection permissionsConfig = initConfig("permissions.yml");
		if ( permissionsConfig != null ) {
			kdc.getPermissionManager().load(new BukkitConfiguration(permissionsConfig));
		} else {
			log("An error occured, cannot reload permissions.yml", Level.WARNING);
		}

		ConfigurationSection chatConfig = initConfig("chat.yml");
		if ( chatConfig != null ) {
			kdc.getChatManager().load(new BukkitConfiguration(chatConfig));
		} else {
			log("An error occured, cannot reload chat.yml", Level.WARNING);
		}

		// remove players that shouldnt be online
		kdc.getOnlinePlayers().stream()
				.filter(p -> !((BukkitPlayer) p).getPlayer().isOnline())
				.forEach(p -> {
					log(p.getName() + " was not correctly removed. Please contact the developer!", Level.WARNING);
					kdc.onQuit(p);
				});

		kdc.getEventManager().dispatch(new PluginReloadEvent());
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
			log(e.getClass().getSimpleName() + ": " + e.getMessage(), Level.WARNING);
			return null;
		}

		return config;
	}

	private final static String[] defaultLanguages = new String[] { "en", "nl" };

	private void loadMessages(String language) {
		File directory = new File(getDataFolder(), "languages");
		if ( !directory.exists() ) {
			directory.mkdirs();
		}

		for ( String lang : defaultLanguages ) {
			File outFile = new File(directory, lang + ".yml");
			if ( !outFile.exists() ) {
				URL inurl = getClassLoader().getResource("languages/" + lang + ".yml");
				try (
						InputStream in = inurl.openStream();
						OutputStream out = new FileOutputStream(outFile)
				){

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		try {
			YamlConfiguration messages = YamlConfiguration.loadConfiguration(new File(directory, language + ".yml"));
			kdc.getMessages().setMessages(new BukkitConfiguration(messages));
		} catch (Exception ex) {
			System.out.println("Unable load custom language file.");
			ex.printStackTrace();
		}

		InputStream fallback = null;
		try {
			File file = new File(directory, language + ".yml");
			if ( file.exists() ) {
				System.out.println("abc");
				fallback = new FileInputStream(file);
			} else {
				fallback = getClassLoader().getResource("languages/en.yml").openStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if ( fallback == null ) {
			return;
		}

		try {
			YamlConfiguration fallbackMessages = YamlConfiguration.loadConfiguration(new InputStreamReader(fallback, StandardCharsets.UTF_8));
			kdc.getMessages().setFallback(new BukkitConfiguration(fallbackMessages));
		} catch (Exception ex) {
			System.out.println("Unable to load fallback language file!");
			ex.printStackTrace();
		} finally {
			try {
				fallback.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
