package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.regex.Pattern;

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
            for ( Kingdom kingdom : plugin.getApi().getKingdomHandler().getKingdoms()) {
                List<Player> members = plugin.getApi().getKingdomHandler().getOnlineMembers(kingdom);
                if ( members.size() == 0 ) continue;

                message += ChatColor.WHITE + kingdom.getDisplay() + ChatColor.GRAY + " (" + members.size() + ")" + ChatColor.WHITE + ": ";

                String line = "";
                for (Player p : members) {
                    KingdomUser user = plugin.getApi().getUserHandler().getUser(p);
                    KingdomRank rank = plugin.getApi().getUserHandler().getRank(user);

                    line += ", ";

                    String fulldisplay = ChatColor.GRAY + "";

                    if ( plugin.getChatManager().hasVault() && plugin.getChatManager().getVault().getPlayerPrefix(p) != null ) {
                        fulldisplay += plugin.getChatManager().getVault().getPlayerPrefix(p) + " ";
                    }

                    if ( rank.getPrefix() != null ) {
                        fulldisplay += rank.getPrefix() + " ";
                    }

                    fulldisplay += p.getDisplayName() + ChatColor.WHITE;

                    if ( plugin.getChatManager().hasVault() && plugin.getChatManager().getVault().getPlayerSuffix(p) != null ) {
                        fulldisplay += plugin.getChatManager().getVault().getPlayerSuffix(p) + " ";
                    }

                    if ( rank.getSuffix() != null ) {
                        fulldisplay += rank.getSuffix() + " ";
                    }

                    line += fulldisplay.replaceAll(Pattern.quote("  "), " ");
                }
                message += line.trim().substring(2) + "\n";
            }

            e.getPlayer().sendMessage(plugin.getPrefix() + plugin.getMsg().getMessage("cmdListNormal", Bukkit.getOnlinePlayers().size() + "") + "\n" + message);
        }
    }
}
