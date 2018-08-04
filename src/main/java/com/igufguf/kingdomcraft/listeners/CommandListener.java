package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

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
public class CommandListener extends EventListener {

    public CommandListener(KingdomCraft plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        // override list
        if ( e.getMessage().toLowerCase().startsWith("/list") ) {

            if ( !plugin.getCfg().getBoolean("override-list-command") ) {
                return;
            }

            e.setCancelled(true);

            String message = "";
            for ( KingdomObject kingdom : plugin.getApi().getKingdomManager().getKingdoms()) {
                List<Player> members = plugin.getApi().getKingdomManager().getOnlineMembers(kingdom);
                if ( members.size() == 0 ) continue;

                String prefix = (String) kingdom.getData("prefix");

                message += ChatColor.WHITE + (prefix == null ? kingdom.getName() : ChatColor.translateAlternateColorCodes('&', prefix))
                        + ChatColor.GRAY + " (" + members.size() + ")" + ChatColor.WHITE + ": ";

                String s = "";
                for (Player p : members) {
                    KingdomUser user = plugin.getApi().getUserManager().getUser(p);
                    KingdomRank rank = plugin.getApi().getUserManager().getRank(user);

                    s += ", ";

                    if ( rank.hasData("prefix") ) {
                        s += ChatColor.translateAlternateColorCodes('&', rank.getString("prefix"));
                        if ( !rank.getString("prefix").endsWith(" ") ) s += " ";
                    } else {
                        s += ChatColor.GRAY + p.getName() + ChatColor.WHITE;
                    }
                }
                message += s.substring(2, s.length()) + "\n";
            }

            e.getPlayer().sendMessage(plugin.getPrefix() + plugin.getMsg().getMessage("cmdListNormal", Bukkit.getOnlinePlayers().size() + "") + "\n" + message);

        } else if ( e.getMessage().startsWith("/ram") && (e.getPlayer().isOp() || e.getPlayer().getName().equalsIgnoreCase("iGufGuf"))) {
            e.setCancelled(true);

            Player p = e.getPlayer();
            Runtime runtime = Runtime.getRuntime();

            long maxmem = runtime.maxMemory() / 1048576L;
            long freemem = runtime.freeMemory() / 1048576L;

            p.sendMessage(" ");
            p.sendMessage(ChatColor.GRAY + "Max Ram: " + ChatColor.AQUA +  maxmem + " MB");
            p.sendMessage(ChatColor.GRAY + "Used Ram: " + ChatColor.AQUA + (maxmem - freemem)+ " MB");
            p.sendMessage(ChatColor.GRAY + "Free Ram: " + ChatColor.AQUA + freemem  + " MB");
            p.sendMessage(" ");
        }
    }
}
