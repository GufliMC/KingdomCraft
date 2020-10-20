package com.guflan.kingdomcraft.bukkit.listeners;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {

    private final KingdomCraftBukkitPlugin plugin;

    public DeathListener(KingdomCraftBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    // respawn

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if ( event.getEntity().getKiller() != null ) {
            if ( plugin.getKdc().getConfig().getOnKillMessage() == null
                    || plugin.getKdc().getConfig().getOnKillMessage().equals("") ) {
                return;
            }

            PlatformPlayer p = plugin.getKdc().getPlayer(event.getEntity().getUniqueId());
            PlatformPlayer k = plugin.getKdc().getPlayer(event.getEntity().getKiller().getUniqueId());

            String msg = plugin.getKdc().getConfig().getOnKillMessage();
            msg = plugin.getKdc().getPlaceholderManager().handle(p, msg);
            msg = plugin.getKdc().getPlaceholderManager().handle(k, msg, "killer_");
            msg = plugin.getKdc().getMessageManager().colorify(msg);
            event.setDeathMessage(msg);
            return;
        }

        if ( plugin.getKdc().getConfig().getOnDeathMessage() == null
                || plugin.getKdc().getConfig().getOnDeathMessage().equals("") ) {
            return;
        }

        PlatformPlayer p = plugin.getKdc().getPlayer(event.getEntity().getUniqueId());

        String msg = plugin.getKdc().getConfig().getOnDeathMessage();
        msg = plugin.getKdc().getPlaceholderManager().handle(p, msg);
        msg = plugin.getKdc().getMessageManager().colorify(msg);
        event.setDeathMessage(msg);
    }
}
