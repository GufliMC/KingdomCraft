package com.igufguf.kingdomcraft.bukkit;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.chat.ChatManager;
import com.igufguf.kingdomcraft.api.domain.Factory;
import com.igufguf.kingdomcraft.api.event.EventManager;
import com.igufguf.kingdomcraft.api.managers.CommandManager;
import com.igufguf.kingdomcraft.api.managers.KingdomManager;
import com.igufguf.kingdomcraft.api.managers.MessageManager;
import com.igufguf.kingdomcraft.api.managers.PlayerManager;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderManager;
import com.igufguf.kingdomcraft.api.scheduler.AbstractScheduler;
import com.igufguf.kingdomcraft.bukkit.chat.BukkitChat;
import com.igufguf.kingdomcraft.bukkit.chat.ChatListener;
import com.igufguf.kingdomcraft.bukkit.command.BukkitCommandExecutor;
import com.igufguf.kingdomcraft.bukkit.domain.BukkitFactory;
import com.igufguf.kingdomcraft.bukkit.listeners.ConnectionListener;
import com.igufguf.kingdomcraft.bukkit.placeholders.BukkitPlaceholderReplacer;
import com.igufguf.kingdomcraft.common.chat.DefaultChatManager;
import com.igufguf.kingdomcraft.common.command.DefaultCommandManager;
import com.igufguf.kingdomcraft.common.event.DefaultEventManager;
import com.igufguf.kingdomcraft.common.kingdom.DefaultKingdomManager;
import com.igufguf.kingdomcraft.common.placeholders.DefaultPlaceholderManager;
import com.igufguf.kingdomcraft.common.player.DefaultPlayerManager;
import com.igufguf.kingdomcraft.common.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
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
	private Factory factory;

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

		Storage storage = new Storage(this);

		this.scheduler = new BukkitScheduler(this);
		this.factory = new BukkitFactory(this);

		this.messageManager = new BukkitMessageManager(this);
		this.commandManager = new DefaultCommandManager(this);
		this.playerManager = new DefaultPlayerManager(this, storage);
		this.kingdomManager = new DefaultKingdomManager(this, storage);
		this.eventManager = new DefaultEventManager();
		this.chatManager = new DefaultChatManager(this);
		this.placeholderManager = new DefaultPlaceholderManager();

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

		for ( Player player : Bukkit.getOnlinePlayers() ) {
			getPlayerManager().join(player.getUniqueId(), player.getName());
		}
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	@Override
	public AbstractScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public Factory getFactory() {
		return factory;
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
}
