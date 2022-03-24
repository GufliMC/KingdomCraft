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
import com.gufli.kingdomcraft.bukkit.chat.DiscordSRVHook;
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
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KingdomCraftBukkitPlugin extends JavaPlugin implements KingdomCraftPlugin {

	private KingdomCraftImpl kdc;

	private final BukkitScheduler scheduler;

	//

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

	private final static Pattern hexColorPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");

	@Override
	public String colorify(String msg) {
		// RGB support: <#rrggbb>
		Matcher matcher = hexColorPattern.matcher(msg);
		while (matcher.find()) {
			String rgb = matcher.group().substring(2).toLowerCase();
			StringBuilder result = new StringBuilder();
			result.append("&x");
			for ( char s : rgb.toCharArray() ) {
				result.append("&").append(s);
			}
			msg = msg.substring(0, matcher.start()) + result + msg.substring(matcher.end());
		}

		msg = StringEscapeUtils.unescapeJava(msg);
		return ChatColor.translateAlternateColorCodes('&', msg);
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

		// item serializer for saving itemstacks in database, must be loaded before ebean initialization
		new BukkitItemSerializer();

		// For some reason, required ebean classes are not in the classpath of the current classloader.
		// Fix this by changing the class loader to the one of the current class.
		ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		// load database
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

		this.kdc = new KingdomCraftImpl(this, context);

		// Load configurations
		load();

		// Load online players
		for ( Player p : Bukkit.getOnlinePlayers() ) {
			this.kdc.onLogin(p.getUniqueId(), p.getName()).accept(new BukkitPlayer(p));
		}

		// commands
		CommandHandler commandHandler = new CommandHandler(this);
		/*
		PluginCommand command = getCommand("kingdomcraft");
		command.setExecutor(commandHandler);
		command.setTabCompleter(commandHandler);
		 */
		try {
			Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			CommandMap commandMap = (CommandMap) f.get(Bukkit.getServer());
			commandMap.register("kingdomcraft", new KingdomCraftCommand<>(this, commandHandler, kdc.getConfig().getCommandAliases()));
		} catch (IllegalAccessException | NoSuchFieldException e) {
			log("Cannot register default command. Shutting down plugin.", Level.SEVERE);
			disable();
			return;
		}

		// listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new FriendlyFireListener(this), this);
		pm.registerEvents(new JoinQuitListener(this), this);
		pm.registerEvents(new RespawnListener(this), this);
		pm.registerEvents(new DeathListener(this), this);
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new PermissionsListener(this), this);
		pm.registerEvents(new MoveListener(this), this);
		pm.registerEvents(new ChatListener(this), this);
		pm.registerEvents(new ConnectionListener(this), this);
		pm.registerEvents(new MenuChatListener(this), this);

		// vault permissions hook
		new VaultPermissionHandler(this);

		// event commands
		new KingdomJoinQuitListener(this);
		new KingdomCreateDeleteListener(this);

		// internal placeholders system
		new PlaceholderReplacer(this);
		new VaultPlaceholderReplacer(this);

		// admin mode join
		new AdminJoinListener(this);

		// dislay names
		new DisplayNameListener(this);

		// discord srv hook
		pm.registerEvents(new DiscordSRVHook(this), this);

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
		if ( kdc != null ) {
			kdc.stop();
		}
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	public void load() {
		ConfigurationSection config = initConfig("config.yml");
		if ( config != null && config.contains("settings") ) {
			config = config.getConfigurationSection("settings");

			kdc.getConfig().load(new BukkitConfiguration(config));
			loadMessages(config.getString("language"));
		} else {
			log("An error occured, cannot load config.yml", Level.WARNING);
			kdc.getChatManager().load(getConfigResource("config.yml"));
		}

		ConfigurationSection permissionsConfig = initConfig("permissions.yml");
		if ( permissionsConfig != null ) {
			kdc.getPermissionManager().load(new BukkitConfiguration(permissionsConfig));
		} else {
			log("An error occured, cannot load permissions.yml", Level.WARNING);
			kdc.getChatManager().load(getConfigResource("permissions.yml"));
		}

		ConfigurationSection chatConfig = initConfig("chat.yml");
		if ( chatConfig != null ) {
			kdc.getChatManager().load(new BukkitConfiguration(chatConfig));
		} else {
			log("An error occured, cannot load chat.yml", Level.WARNING);
			kdc.getChatManager().load(getConfigResource("chat.yml"));
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

	private Configuration getConfigResource(String filename) {
		return new BukkitConfiguration(YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(filename), StandardCharsets.UTF_8)));
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

	private void loadMessages(String language) {
		File directory = new File(getDataFolder(), "languages");
		if ( !directory.exists() ) {
			directory.mkdirs();
		}

		MessagesLoader.load(kdc.getMessages(), getClassLoader(), directory, language);
	}

}
