package com.igufguf.kingdomcraft;

import com.igufguf.kingdomcraft.database.DatabaseManager;
import com.igufguf.kingdomcraft.handlers.CommandHandler;
import com.igufguf.kingdomcraft.handlers.KingdomHandler;
import com.igufguf.kingdomcraft.handlers.MessageHandler;
import com.igufguf.kingdomcraft.handlers.PlayerHandler;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

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
public class KingdomCraft extends JavaPlugin {

	public MessageHandler messageHandler;
	public KingdomHandler kingdomHandler;
	public PlayerHandler playerHandler;
	public CommandHandler commandHandler;

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

		DatabaseManager databaseManager;
		try {
			databaseManager = new DatabaseManager(this, config.getConfigurationSection("database"));
		} catch (Exception ex) {
			ex.printStackTrace();
			getLogger().warning("An error occured with the database setup!");
			disable();
			return;
		}

		messageHandler = new MessageHandler(this);
		kingdomHandler = new KingdomHandler(this, databaseManager.getDatabase());
		playerHandler = new PlayerHandler(this, databaseManager.getDatabase());
		commandHandler = new CommandHandler(this);
	}

	private void disable() {
		this.getPluginLoader().disablePlugin(this);
	}

	/*
	private String prefix = ChatColor.RED + ChatColor.BOLD.toString() + "KingdomCraft" + ChatColor.DARK_GRAY + ChatColor.BOLD + " > " + ChatColor.GRAY;

	private KingdomCraft plugin;

	private final KingdomCraftApi api;
	private final KingdomCraftConfig config;
	private final KingdomCraftMessages messages;

	private ChatManager chatManager;
	private PermissionManager permissionManager;
	private TeleportManager teleportManager;

	public KingdomCraft() {
		this.plugin = this;

		// create data folder
		if ( !getDataFolder().exists() ) {
			getDataFolder().mkdirs();
		}

        this.config = new KingdomCraftConfig(this);
        this.messages = new KingdomCraftMessages(this);

		this.api = new KingdomCraftApi(this);
	}

	@Override
	public void onEnable() {
		// conversion from 4.1.1 to 5.0
		convert();

		api.reload();

		// register command
		PluginCommand cmd = getCommand("kingdom");
		cmd.setExecutor(api.getCommandHandler());
		cmd.setTabCompleter(api.getCommandHandler());

		// load first to register debug executors
		api.getCommandHandler().register(new DebugCommand(this));

		//load defaults
        config.reload();
        messages.reload();

        //load managers
        chatManager = new ChatManager(this);
        permissionManager = new PermissionManager(this);
        teleportManager = new TeleportManager(this);

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ConnectionListener(this), this);
		pm.registerEvents(new MoveListener(this), this);
		pm.registerEvents(new DamageListener(this), this);
		pm.registerEvents(new RespawnListener(this), this);
		pm.registerEvents(new CommandListener(this), this);

		if ( getChatManager().isChatSystemEnabled() )
			pm.registerEvents(new ChatListener(this), this);

		// load the commands
		loadCommands();

		for ( Player p : Bukkit.getOnlinePlayers() ) {
			api.getUserHandler().loadUser(p);
		}

		getLogger().info("Enabled " + this.getDescription().getFullName());
	}

	@Override
	public void onDisable() {
		save();

		for (KingdomUser user : api.getUserHandler().getUsers() ) {
			api.getUserHandler().unloadUser(user);
		}
	}
	
	private void loadCommands() {
		KingdomCommandHandler cmdHandler = getApi().getCommandHandler();

		cmdHandler.register(new HelpCommand(this));

		cmdHandler.register(new InfoCommand(this));
		cmdHandler.register(new ListCommand(this));
		cmdHandler.register(new SpawnCommand(this));

		cmdHandler.register(new JoinCommand(this));
		cmdHandler.register(new LeaveCommand(this));

		cmdHandler.register(new EnemyCommand(this));
		cmdHandler.register(new FriendlyCommand(this));
		cmdHandler.register(new NeutralCommand(this));
		cmdHandler.register(new InviteCommand(this));

		cmdHandler.register(new KickCommand(this));
		cmdHandler.register(new SetRankCommand(this));
		cmdHandler.register(new SetSpawnCommand(this));
		cmdHandler.register(new SetCommand(this));

		cmdHandler.register(new ReloadCommand(this));
		cmdHandler.register(new SocialSpyCommand(this));

		cmdHandler.register(new CreateCommand(this));
		cmdHandler.register(new DeleteCommand(this));
		cmdHandler.register(new EditCommand(this));
		cmdHandler.register(new FlagCommand(this));
		cmdHandler.register(new FlagsCommand(this));

		if ( getChatManager().isChatSystemEnabled() && getChatManager().areChannelsEnabled() ) {
			cmdHandler.register(new ChannelCommand(this));
		}
	}

	public void save() {
		for ( Kingdom ko : api.getKingdomHandler().getKingdoms() ) {
			api.getKingdomHandler().save(ko);
		}

		for ( KingdomUser user : api.getUserHandler().getUsers() ) {
			api.getUserHandler().save(user);
		}
	}

    public KingdomCraft getPlugin() {
        return plugin;
    }

	public KingdomCraftApi getApi() {
		return api;
	}

	public KingdomCraftConfig getCfg() {
		return config;
	}

	public KingdomCraftMessages getMsg() {
		return messages;
	}


	public ChatManager getChatManager() {
	    return chatManager;
    }

    public PermissionManager getPermissionManager() {
	    return permissionManager;
    }

    public TeleportManager getTeleportManager() {
	    return teleportManager;
    }

    public String getPrefix() {
        return prefix;
    }

    // convert kingdomcraft 4.1.1 data to 5.0
    public void convert() {

		// convert users
		File users = new File(getDataFolder(), "users.yml");
		if ( users.exists() ) {
			File usersdata = new File(getDataFolder() + "/data", "users.data");
			convertData(users, usersdata);
			getLogger().info("Conversion of user data successfull!");
		}

		// convert relations
		File relations = new File(getDataFolder(), "relations.yml");
		if ( relations.exists() ) {
			File relationsdata = new File(getDataFolder() + "/data", "relations.data");
			convertData(relations, relationsdata);
			getLogger().info("Conversion of relations data successfull!");
		}

		// convert kingdom
		File directory = new File(getDataFolder() + "/kingdoms");
		if ( directory.exists() ) {
			File newDataFile = new File(getDataFolder(), "kingdoms.yml");
			FileConfiguration newDataConfig = YamlConfiguration.loadConfiguration(newDataFile);

			for (File file : directory.listFiles()) {
				if (file.isDirectory()) continue;
				if (!file.getName().replace(".yml", "").matches("[a-zA-Z]+")) continue;

				String kingdom = file.getName().replace(".yml", "");

				try {
					FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

					// this is no longer saved here anymore
					fc.set("regions", null);

					newDataConfig.set("kingdoms." + kingdom, fc);
					newDataConfig.save(newDataFile);

					Kingdom ko = api.getKingdomHandler().load(kingdom);
					if ( ko == null ) continue;

					// convert spawn
					if ( fc.contains("spawn") ) {
						ko.setSpawn(KingdomUtils.locFromString(fc.getString("spawn")));

						newDataConfig.set("kingdoms." + kingdom + ".spawn", null);
						newDataConfig.save(newDataFile);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				getLogger().info("Conversion of kingdom data for '" + kingdom + "' successfull!");
			}

			directory.renameTo(new File(getDataFolder() + "/.kingdoms"));
		}
	}

	private void convertData(File oldFile, File newFile) {
		try (
				FileInputStream fis = new FileInputStream(oldFile);
				FileOutputStream fos = new FileOutputStream(newFile)
		) {
			if ( !newFile.exists() ) {
				newFile.createNewFile();
			}

			byte[] data = new byte[(int) oldFile.length()];
			fis.read(data);

			// encode to base64
			data = Base64.getEncoder().encode(data);

			fos.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

		oldFile.renameTo(new File(getDataFolder(), "." + oldFile.getName() + ".disabled"));
	}
	*/
}
