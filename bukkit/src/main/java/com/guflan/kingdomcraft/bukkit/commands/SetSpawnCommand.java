package com.guflan.kingdomcraft.bukkit.commands;

import com.guflan.kingdomcraft.api.KingdomCraftHandler;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.bukkit.BukkitKingdomCraftPlugin;
import com.guflan.kingdomcraft.common.command.DefaultCommandBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class SetSpawnCommand extends DefaultCommandBase {

    private final BukkitKingdomCraftPlugin plugin;

    public SetSpawnCommand(KingdomCraftHandler kdc, BukkitKingdomCraftPlugin plugin) {
        super(kdc, "setspawn", 0, true);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setspawn") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        User user = kdc.getUser((Player) sender);
        if (user.getKingdom() == null) {
            kdc.getMessageManager().send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        Location loc = Bukkit.getPlayer(user.getUniqueId()).getLocation();
        plugin.getData().setSpawn(user.getKingdom(), loc);
        plugin.getData().save();

        DecimalFormat df = new DecimalFormat("#.0");
        String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());
        kdc.getMessageManager().send(sender, "cmdSetSpawnSuccess", str);
    }
}
