package com.guflan.kingdomcraft.bukkit.bridge;

import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.managers.MessageManager;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;
import com.guflan.kingdomcraft.bukkit.KingdomCraft;
import com.guflan.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.guflan.kingdomcraft.common.AbstractKingdomCraftBridge;
import com.guflan.kingdomcraft.common.storage.Storage;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BukkitKingdomCraftBridge extends AbstractKingdomCraftBridge {

    private KingdomCraft plugin;

    private AbstractScheduler scheduler;
    private MessageManager messageManager;

    public BukkitKingdomCraftBridge(KingdomCraft plugin, Storage storage) {
        super(storage);

        this.plugin = plugin;
        this.scheduler = new BukkitScheduler(plugin);
        this.messageManager = new BukkitMessageManager(plugin);
    }

    @Override
    public AbstractScheduler getScheduler() {
        return this.getScheduler();
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
    public void log(String msg) {
        plugin.getLogger().info(msg);
    }

    @Override
    public void log(String msg, Level level) {
        plugin.getLogger().log(level, msg);
    }

}
