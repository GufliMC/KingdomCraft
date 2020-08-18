package com.igufguf.kingdomcraft.bukkit;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.managers.CommandManager;
import com.igufguf.kingdomcraft.api.managers.KingdomManager;
import com.igufguf.kingdomcraft.api.managers.MessageManager;
import com.igufguf.kingdomcraft.api.managers.PlayerManager;
import com.igufguf.kingdomcraft.api.scheduler.AbstractScheduler;
import com.igufguf.kingdomcraft.bukkit.command.BukkitCommandExecutor;
import com.igufguf.kingdomcraft.bukkit.listeners.ConnectionListener;
import com.igufguf.kingdomcraft.common.command.DefaultCommandManager;
import com.igufguf.kingdomcraft.common.managers.DefaultKingdomManager;
import com.igufguf.kingdomcraft.common.managers.DefaultPlayerManager;
import com.igufguf.kingdomcraft.common.storage.Storage;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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

		this.messageManager = new BukkitMessageManager(this);
		this.commandManager = new DefaultCommandManager(this);
		this.playerManager = new DefaultPlayerManager(this, storage);
		this.kingdomManager = new DefaultKingdomManager(this, storage);

		// commands
		BukkitCommandExecutor commandHandler = new BukkitCommandExecutor(this);
		PluginCommand command = getCommand("kingdomcraft");
		command.setExecutor(commandHandler);
		command.setTabCompleter(commandHandler);

		// listeners
		getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	@Override
	public AbstractScheduler getScheduler() {
		return this.scheduler;
	}

	@Override
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}

	@Override
	public KingdomManager getKingdomManager() {
		return this.kingdomManager;
	}

	@Override
	public MessageManager getMessageManager() {
		return this.messageManager;
	}

	@Override
	public CommandManager getCommandManager() {
		return this.commandManager;
	}
}
