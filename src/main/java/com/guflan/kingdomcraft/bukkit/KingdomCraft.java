package com.guflan.kingdomcraft.bukkit;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.bukkit.chat.BukkitChat;
import com.guflan.kingdomcraft.bukkit.command.BukkitCommandExecutor;
import com.guflan.kingdomcraft.bukkit.listeners.ConnectionListener;
import com.guflan.kingdomcraft.bukkit.placeholders.BukkitPlaceholderReplacer;
import com.guflan.kingdomcraft.common.chat.DefaultChatManager;
import com.guflan.kingdomcraft.common.command.DefaultCommandManager;
import com.guflan.kingdomcraft.common.event.DefaultEventManager;
import com.guflan.kingdomcraft.common.kingdom.DefaultKingdomManager;
import com.guflan.kingdomcraft.common.placeholders.DefaultPlaceholderManager;
import com.guflan.kingdomcraft.common.kingdom.DefaultPlayerManager;
import com.guflan.kingdomcraft.common.storage.Storage;
import com.guflan.kingdomcraft.common.storage.implementation.EBeanStorageImplementation;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.managers.CommandManager;
import com.guflan.kingdomcraft.api.managers.KingdomManager;
import com.guflan.kingdomcraft.api.managers.MessageManager;
import com.guflan.kingdomcraft.api.managers.PlayerManager;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Copyrighted 2020 iGufGuf
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
public class KingdomCraft extends JavaPlugin implements KingdomCraftPlugin {

	private BukkitScheduler scheduler;

	private KingdomManager kingdomManager;
	private PlayerManager playerManager;
	private CommandManager commandManager;
	private MessageManager messageManager;
	private EventManager eventManager;
	private ChatManager chatManager;
	private PlaceholderManager placeholderManager;

	@Override
	public void onEnable() {
		// LOAD CONFIG

		YamlConfiguration config = new YamlConfiguration();

		File configFile = new File(this.getDataFolder(), "config.yml");
		if ( !configFile.exists() ) {
			saveResource("config.yml", true);
			//configFile = new File(this.getDataFolder(), "config.yml");
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

        ConfigurationSection dbConfig = config.getConfigurationSection("database");
		EBeanStorageImplementation impl = new EBeanStorageImplementation(
				this,
				dbConfig.getString("url"),
                dbConfig.getString("driver"),
                dbConfig.getString("username"),
                dbConfig.getString("password")
		);
		Storage storage = new Storage(this, impl);

		this.scheduler = new BukkitScheduler(this);

		this.messageManager = new BukkitMessageManager(this);
		this.commandManager = new DefaultCommandManager(this);
		this.playerManager = new DefaultPlayerManager(this, storage);
		this.kingdomManager = new DefaultKingdomManager(this, storage);
		this.eventManager = new DefaultEventManager();
		this.chatManager = new DefaultChatManager(this);
		this.placeholderManager = new DefaultPlaceholderManager(this);

		new BukkitPlaceholderReplacer(this);
		new BukkitChat(this);

		// commands
		BukkitCommandExecutor commandHandler = new BukkitCommandExecutor(this);
		PluginCommand command = getCommand("kingdomcraft");
		command.setExecutor(commandHandler);
		command.setTabCompleter(commandHandler);

		// listeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ConnectionListener(this), this);
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	@Override
	public AbstractScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	@Override
	public KingdomManager getKingdomManager() {
		return kingdomManager;
	}

	@Override
	public MessageManager getMessageManager() {
		return messageManager;
	}

	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public ChatManager getChatManager() {
		return chatManager;
	}

	@Override
	public PlaceholderManager getPlaceholderManager() {
		return placeholderManager;
	}

	@Override
	public String translateColors(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	@Override
	public String stripColors(String msg) {
		return ChatColor.stripColor(msg);
	}

	@Override
	public void log(String msg) {
		getLogger().info(msg);
	}


}
