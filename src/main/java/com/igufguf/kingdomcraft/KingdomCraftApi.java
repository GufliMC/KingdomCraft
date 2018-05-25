package com.igufguf.kingdomcraft;

import com.igufguf.kingdomcraft.events.KingdomJoinEvent;
import com.igufguf.kingdomcraft.events.KingdomLeaveEvent;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomRelation;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Copyrighted 2017 iGufGuf
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
 * along with KingdomCraft. If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class KingdomCraftApi {

    private final List<KingdomObject> kingdoms = Collections.synchronizedList(new ArrayList<KingdomObject>());
    private final List<KingdomUser> users = Collections.synchronizedList(new ArrayList<KingdomUser>());

    private final FileConfiguration relations;

    KingdomCraftApi(KingdomCraft plugin) {
        File usersfile = new File(plugin.getDataFolder(), "users.yml");
        if ( !usersfile.exists() ) {
            try {
                if ( !usersfile.createNewFile() ) {
                    System.out.println("[KingdomCraft] Couldn't create users.yml please contact the developer!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File relationsfile = new File(plugin.getDataFolder(), "relations.yml");
        if ( !relationsfile.exists() ) {
            try {
                if ( !relationsfile.createNewFile() ) {
                    System.out.println("[KingdomCraft] Couldn't create relations.yml please contact the developer!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        relations = YamlConfiguration.loadConfiguration(relationsfile);

        File kingdomsfolder = new File(plugin.getDataFolder(), "/kingdoms/");
        if ( kingdomsfolder.isDirectory() ) {
            for ( File file : kingdomsfolder.listFiles() ) {
                if ( file.getName().endsWith(".yml") ) {
                    KingdomObject ko = new KingdomObject(file.getName().replace(".yml", ""), YamlConfiguration.loadConfiguration(file));
                    kingdoms.add(ko);
                }
            }
        } else if ( !kingdomsfolder.exists() ) {
            kingdomsfolder.mkdirs();
            createKingdom("jenava"); // default kingdom
        }
    }

    public boolean createKingdom(String name) {
        File newkd = new File(KingdomCraft.getPlugin().getDataFolder(), "/kingdoms/" + name + ".yml");
        if ( !newkd.exists() ) {
            try {
                newkd.createNewFile();
                FileConfiguration newkdc = YamlConfiguration.loadConfiguration(newkd);

                newkdc.set("prefix", "&7[&" + "abcdef0123456789".split("")[new Random().nextInt(16)] + name + "&7]");
                newkdc.set("ranks.king.prefix", "&7[&6King&7]");
                newkdc.set("ranks.king.permissions", Arrays.asList("member", "kingdom.kick", "kingdom.invite"));
                newkdc.set("ranks.member.prefix", "&7[&8Member&7]");
                newkdc.set("ranks.member.default", true);
                newkdc.set("ranks.member.permissions", Arrays.asList("kingdom.spawn", "kingdom.channel"));

                newkdc.save(newkd);

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<KingdomObject> getKingdoms() {
        return new ArrayList<>(kingdoms);
    }

    public List<KingdomUser> getUsers() {
        return new ArrayList<>(users);
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

        return getOfflineUser(Bukkit.getOfflinePlayer(username).getUniqueId());
    }

    public KingdomUser getOfflineUser(UUID uuid) {
        File usersfile = new File(KingdomCraft.getPlugin().getDataFolder(), "users.yml");
        FileConfiguration usersconfig = YamlConfiguration.loadConfiguration(usersfile);

        if ( usersconfig.get(uuid.toString()) == null ) {
            return new KingdomUser(Bukkit.getPlayer(uuid) != null ? Bukkit.getPlayer(uuid).getName() :
                    Bukkit.getOfflinePlayer(uuid).getName(), uuid.toString());
        }

        String name = usersconfig.getString(uuid.toString() + ".name");

        KingdomUser ku = new KingdomUser(name, uuid.toString());

        for ( String key : usersconfig.getConfigurationSection(uuid.toString()).getKeys(false) ) {
            if ( key.equalsIgnoreCase("name") ) continue;
            ku.setData(key, usersconfig.get(uuid.toString() + "." + key));
            ku.delInLocalList("changes", key);
        }

        if ( ku.getRank() == null && ku.getKingdom() != null ) {
            setRank(ku, ku.getKingdom().getDefaultRank());
        }

        return ku;
    }

    public List<KingdomUser> getAllUsers() {
        File usersfile = new File(KingdomCraft.getPlugin().getDataFolder(), "users.yml");
        FileConfiguration usersconfig = YamlConfiguration.loadConfiguration(usersfile);

        List<KingdomUser> users = new ArrayList<>();
        
        for ( String uuid : usersconfig.getKeys(false) ) {
            KingdomUser user = new KingdomUser(usersconfig.getString(uuid + ".name"), uuid);

            for ( String key : usersconfig.getConfigurationSection(uuid).getKeys(false) ) {
                if ( key.equalsIgnoreCase("name") ) continue;
                user.setData(key, usersconfig.get(uuid + "." + key));
            }

            users.add(user);
        }
        
        return users;
    }

    public void refreshPermissions(KingdomUser user) {
        PermissionAttachment pa = user.hasLocalData("permissions") ? (PermissionAttachment) user.getLocalData("permissions") : null;
        if ( pa != null ) pa.remove();

        KingdomRank rank = user.getRank();
        if ( rank == null || !enabledWorld(user.getPlayer().getWorld())) return;

        pa = user.getPlayer().addAttachment(KingdomCraft.getPlugin());

        for ( String perm : rank.getPermissions() ) {
            if ( perm.startsWith("-") ) {
                pa.setPermission(perm.replaceFirst(Pattern.quote("-"), "").trim(), false);
            } else {
                pa.setPermission(perm.trim(), true);
            }
        }

        user.setLocalData("permissions", pa);
    }

    public KingdomObject getKingdom(String name) {
        for ( KingdomObject ko : kingdoms ) {
            if ( ko.getName().equalsIgnoreCase(name) ) return ko;
        }
        return null;
    }

    public void setKingdom(KingdomUser user, KingdomObject kingdom) {
        if ( kingdom == null ) {
            KingdomObject oldKingdom = user.getKingdom();
            KingdomRank oldRank = user.getRank();

            user.setData("kingdom", null);
            user.setData("rank", null);
            user.setData("channels", null);

            Bukkit.getPluginManager().callEvent(new KingdomLeaveEvent(user, oldKingdom, oldRank));

            KingdomCraft.getPlugin().save(user);
            return;
        }

        user.setData("kingdom", kingdom.getName());
        if ( kingdom.hasData("defaultrank") ) user.setData("rank", kingdom.getData("defaultrank"));

        if ( user.hasData("invites") ) {
            List<String> invites = (List<String>) user.getData("invites");
            invites.remove(kingdom.getName());
        }

        refreshPermissions(user);

        Bukkit.getPluginManager().callEvent(new KingdomJoinEvent(user));

        KingdomCraft.getPlugin().save(user);
    }

    public void setRank(KingdomUser user, KingdomRank rank) {
        KingdomObject ko = user.getKingdom();
        if ( ko == null || !ko.hasRank(rank) ) return;

        user.setData("rank", rank.getName());
        refreshPermissions(user);
    }

    public KingdomRelation getRelation(KingdomObject kingdom, KingdomObject target) {
        String relname = null;
        if ( relations.contains(kingdom.getName() + "-" + target.getName()) ) {
            relname = relations.getString(kingdom.getName() + "-" + target.getName());
        }
        /*else if ( relations.contains(target.getName() + "-" + kingdom.getName()) ) {
            relname = relations.getString(target.getName() + "-" + kingdom.getName());
        }*/
        if ( relname == null ) return KingdomRelation.NEUTRAL;

        for ( KingdomRelation rel : KingdomRelation.values() ) {
            if ( rel.name().equalsIgnoreCase(relname) ) return rel;
        }
        return KingdomRelation.NEUTRAL;
    }

    public void setRelation(KingdomObject kingdom, KingdomObject target, KingdomRelation relation) {
        //relations.set(target.getName() + "-" + kingdom.getName(), null);
        relations.set(kingdom.getName() + "-" + target.getName(), relation != null ? relation.name() : null);

        try {
            relations.save(new File(com.igufguf.kingdomcraft.KingdomCraft.getPlugin().getDataFolder(), "relations.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<KingdomObject, KingdomRelation> getRelations(KingdomObject kingdom) {
        HashMap<KingdomObject, KingdomRelation> relations = new HashMap<>();
        for ( KingdomObject ko : getKingdoms() ) {
            if ( ko != kingdom ) relations.put(ko, getRelation(kingdom, ko));
        }
        return relations;
    }

    public boolean enabledWorld(World world) {
        if ( !com.igufguf.kingdomcraft.KingdomCraft.getConfg().getBoolean("worlds") ) return true;
        List<String> worlds = com.igufguf.kingdomcraft.KingdomCraft.getConfg().getStringList("world-list");

        for ( String w : worlds ) {
            if ( w.equalsIgnoreCase(world.getName()) ) return true;
        }
        return false;
    }

    public void registerUser(KingdomUser user) {
        if ( !users.contains(user) ) users.add(user);

        System.out.println(user.getName() + " registered (uuid = " + user.getUuid() + ")");
    }

    public void unregisterUser(KingdomUser user) {
        users.remove(user);
    }
}
