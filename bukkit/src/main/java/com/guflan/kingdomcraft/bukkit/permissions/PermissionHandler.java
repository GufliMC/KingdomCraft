package com.guflan.kingdomcraft.bukkit.permissions;

import com.guflan.kingdomcraft.api.domain.RankPermissionGroup;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import com.guflan.kingdomcraft.common.permissions.PermissionGroup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PermissionHandler {

    private final static String PERMISSIONS_KEY = "permission_attachement";

    private final KingdomCraftBukkitPlugin plugin;

    private PermissionGroup defaultGroup;

    public PermissionHandler(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new PermissionsListener(plugin, this), plugin);
        load();

        plugin.getKdc().getCommandManager().registerCommand(new CommandBaseImpl(plugin.getKdc(), "perms", 0, true) {
            @Override
            public void execute(PlatformSender sender, String[] args) {
                PlatformPlayer p = (PlatformPlayer) sender;
                if ( !p.has(PERMISSIONS_KEY) ) {
                    sender.sendMessage("none");
                    return;
                }

                PermissionAttachment pa = p.get(PERMISSIONS_KEY, PermissionAttachment.class);
                sender.sendMessage(plugin.getKdc().getMessageManager().colorify(pa.getPermissions()
                        .keySet().stream()
                        .map(key -> pa.getPermissions().get(key) ? "&a" + key : "&c" + key)
                        .collect(Collectors.joining(", "))));
            }
        });
    }

    private void load() {
        YamlConfiguration groups = new YamlConfiguration();

        File groupsFile = new File(plugin.getDataFolder(), "groups.yml");
        if ( !groupsFile.exists() ) {
            plugin.saveResource("groups.yml", true);
        }

        try {
            groups.load(groupsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            plugin.log("Invalid groups.yml file, please check your syntax!", Level.WARNING);
            return;
        }

        for ( String key : groups.getKeys(false) ) {
            ConfigurationSection cs = groups.getConfigurationSection(key);
            PermissionGroup group = new PermissionGroup(key,
                    cs.contains("permissions") ? cs.getStringList("permissions") : new ArrayList<>(),
                    cs.contains("inheritances") ? cs.getStringList("inheritances") : new ArrayList<>(),
                    cs.contains("worlds") ? cs.getStringList("worlds") : new ArrayList<>());

            if ( key.equalsIgnoreCase("default") ) {
                defaultGroup = group;
                continue;
            }

            plugin.getKdc().getPermissionManager().addPermissionGroup(group);
        }
    }

    void clear(PlatformPlayer player) {
        if ( !player.has(PERMISSIONS_KEY) ) {
            return;
        }

        PermissionAttachment pa = player.get(PERMISSIONS_KEY, PermissionAttachment.class);
        Player p = ((BukkitPlayer) player).getPlayer();
        p.removeAttachment(pa);
    }

    void update(PlatformPlayer player) {
        clear(player);

        User user = plugin.getKdc().getUser(player);

        List<PermissionGroup> groups = new ArrayList<>();
        if ( user.getRank() == null  ) {
            if ( defaultGroup == null ) {
                return;
            }

            groups.add(defaultGroup);
        } else {
            for (RankPermissionGroup rpg : user.getRank().getPermissionGroups()) {
                PermissionGroup group = plugin.getKdc().getPermissionManager().getGroup(rpg.getName());
                if ( group != null ) {
                    groups.add(group);
                }
            }
        }

        Map<String, Boolean> permissions = new HashMap<>();
        groups.forEach(group -> permissions.putAll(plugin.getKdc().getPermissionManager().getTotalPermissions(group)));

        Player p = ((BukkitPlayer) player).getPlayer();
        PermissionAttachment pa = p.addAttachment(plugin);
        permissions.forEach(pa::setPermission);
        player.set(PERMISSIONS_KEY, pa);
    }

}
