package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.commands.executors.admin.DebugCommand;
import com.igufguf.kingdomcraft.events.KingdomJoinEvent;
import com.igufguf.kingdomcraft.events.KingdomLeaveEvent;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

/**
 * Copyrighted 2018 iGufGuf
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
public class UserManager {

    private final KingdomCraftApi api;

    private final List<KingdomUser> users = Collections.synchronizedList(new ArrayList<KingdomUser>());

    private final File file;
    private final FileConfiguration data;

    public UserManager(KingdomCraftApi api) throws IOException {
        this.api = api;

        data = new YamlConfiguration();
        file = new File(api.getPlugin().getDataFolder() + "/data", "users.data");
        if ( !file.exists() ) {
            file.createNewFile();

            File oldFile = new File(api.getPlugin().getDataFolder(), "users.yml");

            if ( oldFile.exists() ) {
                try (
                    FileInputStream fis = new FileInputStream(oldFile);
                    FileOutputStream fos = new FileOutputStream(file)
                ) {
                    int length = fis.available();
                    if ( length != 0 ) {
                        byte[] data = new byte[length];
                        fis.read(data);
                        fos.write(Base64.getEncoder().encode(data));
                    }
                }
                oldFile.delete();
            }
            return;
        }

        try (
            FileInputStream fis = new FileInputStream(file)
        ) {
            int length = fis.available();
            if ( length != 0 ) {
                byte[] data = new byte[length];
                fis.read(data);

                String yaml = new String(Base64.getDecoder().decode(data));
                this.data.loadFromString(yaml);
            }
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        // load debugger

        DebugCommand debugger = (DebugCommand) api.getPlugin().getCmdHandler().getByCommand("debug");
        debugger.register(new DebugCommand.DebugExecutor("user") {
            @Override
            public void onExecute(CommandSender sender, String[] args) {
                if ( args.length == 0 ) return;

                if ( args[0].equalsIgnoreCase("list") ) {
                    String s = "";
                    for ( KingdomUser user : getUsers() ) {
                        s += ", " + ChatColor.GOLD + user.getName() + ChatColor.GRAY;
                    }
                    s = s.substring(2);
                    sender.sendMessage(s);
                    return;
                }

                if ( args.length < 2 ) return;

                KingdomUser user = getUser(args[1]);
                if ( user == null ) user = getOfflineUser(args[1]);
                if ( user == null ){
                    sender.sendMessage(ChatColor.RED + "User cannot be found!");
                    return;
                }

                if ( args[0].equalsIgnoreCase("keys") ) {

                    String s = "";
                    for ( String key : user.getDataMap().keySet() ) {
                        s += ", " + ChatColor.GOLD + key + ChatColor.GRAY;
                    }
                    if ( s.length() > 0 ) s = s.substring(2);

                    String s1 = "";
                    for ( String key : user.getLocalDataMap().keySet() ) {
                        s1 += ", " + ChatColor.GOLD + key + ChatColor.GRAY;
                    }
                    if ( s1.length() > 0 ) s1 = s1.substring(2);

                    sender.sendMessage(ChatColor.RED + "Data: " + ChatColor.GRAY + s);
                    sender.sendMessage(" ");
                    sender.sendMessage(ChatColor.RED + "Local Data: " + ChatColor.GRAY + s1);

                } else if ( args[0].equalsIgnoreCase("show") ) {
                    if ( args.length != 3 ) return;

                    Object v = user.getData(args[2]);
                    Object v1 = user.getLocalData(args[2]);

                    if ( v == null && v1 == null ) {
                        sender.sendMessage(ChatColor.RED + "That key does not exist for that user!");
                        return;
                    }


                    if ( v != null ) {
                        sender.sendMessage(ChatColor.RED + "Data: " + ChatColor.GOLD + DebugCommand.prettyPrint(v));
                    }

                    if ( v1 != null ) {
                        sender.sendMessage(ChatColor.RED + "Local Data: " + ChatColor.GOLD + DebugCommand.prettyPrint(v1));
                    }

                }

            }
        });
    }

    public List<KingdomUser> getUsers() {
        return new ArrayList<>(users);
    }

    public void registerUser(KingdomUser user) {
        if ( !users.contains(user) ) users.add(user);
        api.getPermissionManager().refresh(user);

        System.out.println(user.getName() + " registered (uuid = " + user.getUuid() + ")");
    }

    public void unregisterUser(KingdomUser user) {
        users.remove(user);
        api.getPermissionManager().clear(user);
    }

    public KingdomUser getUser(Player p) {
        if ( p == null || !p.isOnline() ) return (p != null ? getOfflineUser(p.getUniqueId()) : null);
        for ( KingdomUser ku : users ) {
            if ( ku.getUuid().equals(p.getUniqueId().toString()) ) return ku;
        }
        return null;
    }

    public KingdomUser getUser(String username) {
        Player p = Bukkit.getPlayerExact(username);
        if ( p != null ) return getUser(p);

        return getOfflineUser(username);
    }

    public KingdomUser getOfflineUser(String username) {
        for ( String uuid : data.getKeys(false) ) {
            if ( data.contains(uuid + ".name") && data.getString(uuid + ".name").equalsIgnoreCase(username) ) return getOfflineUser(UUID.fromString(uuid));
        }
        return null;
    }

    public KingdomUser getOfflineUser(UUID uuid) {
        if ( data.get(uuid.toString()) == null ) {
            String name = Bukkit.getPlayer(uuid) != null ? Bukkit.getPlayer(uuid).getName() : Bukkit.getOfflinePlayer(uuid).getName();
            return new KingdomUser(name, uuid.toString());
        }

        String name = data.getString(uuid.toString() + ".name");

        KingdomUser ku = new KingdomUser(name, uuid.toString());

        for ( String key : data.getConfigurationSection(uuid.toString()).getKeys(false) ) {
            if ( key.equalsIgnoreCase("name") ) continue;

            Object value = data.get(uuid.toString() + "." + key);
            if ( value instanceof MemorySection ) {
                ku.setData(key, ((MemorySection) value).getValues(false));
            } else {
                ku.setData(key, value);
            }

            ku.delInLocalList("changes", key);
        }

        // failsaves on removed kingdoms / ranks while user was offline
        if ( ku.getKingdom() != null && getKingdom(ku) == null ) {
            ku.setData("kingdom", null);
        }

        // if rank doesn't exists, empty it
        if ( ku.getRank() != null && getRank(ku) == null ) {
            ku.setData("rank", null);
        }

        // set user in default rank if no rank was set and he is in a kingdom
        if ( getKingdom(ku) != null && ku.getRank() == null ) {
            setDefaultRank(ku);
        }

        // convert to new channels system
        if ( ku.getData("channels") instanceof List ) {
            Map<String, Boolean> channels = new HashMap<>();
            for ( String ch : ku.getList("channels", String.class) ) channels.put(ch, true);
            ku.setData("channels", channels);
        }

        return ku;
    }

    public List<KingdomUser> getAllUsers() {
        List<KingdomUser> users = new ArrayList<>();

        for ( String uuid : data.getKeys(false) ) {
            KingdomUser user = new KingdomUser(data.getString(uuid + ".name"), uuid);

            for ( String key : data.getConfigurationSection(uuid).getKeys(false) ) {
                if ( key.equalsIgnoreCase("name") ) continue;
                user.setData(key, data.get(uuid + "." + key));
            }

            users.add(user);
        }

        return users;
    }

    public KingdomObject getKingdom(KingdomUser user) {
        return api.getKingdomManager().getKingdom(user.getKingdom());
    }

    public KingdomRank getRank(KingdomUser user) {
        KingdomObject ko = api.getKingdomManager().getKingdom(user.getKingdom());
        if ( ko == null ) return null;

        return ko.getRank(user.getRank());
    }

    public void setKingdom(KingdomUser user, KingdomObject kingdom) {
        if ( kingdom == null ) {
            KingdomObject oldKingdom = getKingdom(user);
            KingdomRank oldRank = getRank(user);

            user.setData("kingdom", null);
            user.setData("rank", null);
            user.setData("channels", null);

            Bukkit.getPluginManager().callEvent(new KingdomLeaveEvent(user, oldKingdom, oldRank));

            save(user);
            return;
        }

        user.setData("kingdom", kingdom.getName());
        setDefaultRank(user);

        if ( user.hasData("invites") ) {
            List<String> invites = (List<String>) user.getData("invites");
            invites.remove(kingdom.getName());
        }

        api.getPermissionManager().refresh(user);

        Bukkit.getPluginManager().callEvent(new KingdomJoinEvent(user));

        save(user);
    }

    public void setRank(KingdomUser user, KingdomRank rank) {
        KingdomObject ko = getKingdom(user);
        if ( ko == null || !ko.hasRank(rank) ) return;

        user.setData("rank", rank.getName());
        api.getPermissionManager().refresh(user);
    }

    public void setDefaultRank(KingdomUser user) {
        KingdomObject kingdom = getKingdom(user);
        if ( kingdom == null  ) return;

        if ( kingdom.getDefaultRank() != null ) {
            user.setData("rank", kingdom.getDefaultRank().getName());
        } else {
            user.setData("rank", kingdom.getRanks().get(0).getName());
        }
    }

    public void save(KingdomUser user) {
        data.set(user.getUuid() + ".name", user.getName());

        if ( user.hasLocalData("changes") ) {
            for (String key : user.getLocalList("changes", String.class)) {
                if (!user.hasData(key)) {
                    data.set(user.getUuid() + "." + key, null);
                } else {
                    data.set(user.getUuid() + "." + key, user.getData(key));
                }
            }
        }

        String yaml = this.data.saveToString();
        byte[] base64 = Base64.getEncoder().encode(yaml.getBytes());

        try (
            FileOutputStream fos = new FileOutputStream(file)
        ) {
            fos.write(base64);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
