package com.guflan.kingdomcraft.bukkit.bridge;

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;
import com.guflan.kingdomcraft.api.storage.Storage;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.guflan.kingdomcraft.common.AbstractKingdomCraft;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BukkitKingdomCraft extends AbstractKingdomCraft {

    private final Plugin plugin;
    private final AbstractScheduler scheduler;

    private MessageManager messageManager;

    public BukkitKingdomCraft(Plugin plugin, BukkitScheduler scheduler, Storage storage) {
        super(storage);

        this.plugin = plugin;
        this.scheduler = scheduler;

        this.messageManager = new BukkitMessageManager(plugin);
    }

    @Override
    public AbstractScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public MessageManager getMessageManager() {
        return this.getMessageManager();
    }

    @Override
    public List<Player> getOnlinePlayers() {
        return plugin.getServer().getOnlinePlayers().stream().map(BukkitPlayer::new).collect(Collectors.toList());
    }

    @Override
    public Player getPlayer(UUID uuid) {
        org.bukkit.entity.Player bplayer = Bukkit.getPlayer(uuid);
        if ( bplayer == null ) {
            return null;
        }

        return new BukkitPlayer(bplayer);
    }

    @Override
    public Player getPlayer(User user) {
        return getPlayer(user.getUniqueId());
    }

    @Override
    public void log(String msg) {
        plugin.getLogger().info(msg);
    }

    @Override
    public void log(String msg, Level level) {
        plugin.getLogger().log(level, msg);
    }

}
