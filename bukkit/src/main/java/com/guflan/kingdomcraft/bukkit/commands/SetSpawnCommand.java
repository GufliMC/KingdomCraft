package com.guflan.kingdomcraft.bukkit.commands;

import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class SetSpawnCommand extends CommandBaseImpl {

    private final KingdomCraftBukkitPlugin plugin;

    public SetSpawnCommand(KingdomCraftBukkitPlugin plugin) {
        super(plugin.getKdc(), "setspawn", 0, true);
        this.plugin = plugin;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setspawn") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        if (user.getKingdom() == null) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Location loc = Bukkit.getPlayer(user.getUniqueId()).getLocation();
//        plugin.getData().setSpawn(user.getKingdom(), loc);
//        plugin.getData().save();

        DecimalFormat df = new DecimalFormat("#.0");
        String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());
        kdc.getMessageManager().send(sender, "cmdSetSpawnSuccess", str);
    }
}
