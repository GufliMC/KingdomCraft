package com.guflan.kingdomcraft.bukkit.bridge;

import com.guflan.kingdomcraft.api.KingdomCraftPlugin;
import com.guflan.kingdomcraft.api.domain.DomainContext;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import com.guflan.kingdomcraft.common.AbstractKingdomCraft;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitKingdomCraft extends AbstractKingdomCraft {

    private final BukkitKingdomCraftPlugin plugin;

    private final MessageManager messageManager;

    private final List<Player> onlinePlayers = new ArrayList<>();

    public BukkitKingdomCraft(BukkitKingdomCraftPlugin plugin, DomainContext context) {
        super(context);
        this.plugin = plugin;

        this.messageManager = new BukkitMessageManager(plugin);
    }

    @Override
    public KingdomCraftPlugin getPlugin() {
        return plugin;
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return onlinePlayers.stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public Player getPlayer(User user) {
        return getPlayer(user.getUniqueId());
    }

    @Override
    public void join(Player player) {
        super.join(player);
        onlinePlayers.add(player);
    }

    @Override
    public void quit(Player player) {
        super.quit(player);
        onlinePlayers.remove(player);
    }
}
