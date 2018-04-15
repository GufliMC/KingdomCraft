package com.igufguf.kingdomcraft.listeners;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.KingdomCraftMessages;
import com.igufguf.kingdomcraft.objects.KingdomUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        if ( e.getMessage().toLowerCase().startsWith("/list") ) {
            e.setCancelled(true);

            String message = "";
            for ( KingdomObject kd : KingdomCraft.getApi().getKingdoms()) {
                if ( kd.getOnlineMembers().size() > 0 ) {
                    String prefix = (String) kd.getData("prefix");

                    message += ChatColor.WHITE + (prefix == null ? kd.getName() : ChatColor.translateAlternateColorCodes('&', prefix))
                            + ChatColor.GRAY + " (" + kd.getOnlineMembers().size() + ")" + ChatColor.WHITE + ": ";

                    String members = "";
                    for (Player p : kd.getOnlineMembers()) {
                        KingdomUser user = KingdomCraft.getApi().getUser(p);
                       members += ", " + (user.getRank().hasData("prefix") ? ChatColor.translateAlternateColorCodes('&', (String) user.getRank().getData("prefix"))
                                + ( !((String) user.getRank().getData("prefix")).endsWith(" ") ? " " : "") : "") + ChatColor.GRAY + p.getName() + ChatColor.WHITE;
                    }
                    message += members.replaceFirst(", ", "") + "\n";
                }
            }

            e.getPlayer().sendMessage(KingdomCraft.prefix + KingdomCraft.getMsg().getMessage("cmdListNormal", Bukkit.getOnlinePlayers().size() + "") + "\n" + message);

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
