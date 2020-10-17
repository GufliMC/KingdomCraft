package com.guflan.kingdomcraft.bukkit.commands;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.KingdomAttribute;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkit;
import com.guflan.kingdomcraft.bukkit.util.LocationSerializer;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class SetSpawnOtherCommand extends CommandBaseImpl {

    public SetSpawnOtherCommand(KingdomCraftBukkit kdc) {
        super(kdc, "setspawn", 1, true);
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.setspawn") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if (kingdom == null) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        KingdomAttribute attribute = kingdom.getOrCreateAttribute("spawn");

        Location loc = Bukkit.getPlayer(((PlatformPlayer) sender).getUniqueId()).getLocation();
        attribute.setValue(LocationSerializer.serialize(loc));
        kdc.save(attribute);

        DecimalFormat df = new DecimalFormat("#");
        String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

        kdc.getMessageManager().send(sender, "cmdSetSpawnOtherSuccess", kingdom.getName(), str);
    }
}
