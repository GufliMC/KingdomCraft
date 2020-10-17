package com.guflan.kingdomcraft.bukkit.commands;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.KingdomAttribute;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkit;
import com.guflan.kingdomcraft.bukkit.util.LocationSerializer;
import com.guflan.kingdomcraft.bukkit.util.Teleporter;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class SpawnOtherCommand extends CommandBaseImpl {

    public SpawnOtherCommand(KingdomCraftBukkit kdc) {
        super(kdc, "spawn", 1, true);
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        if ( !sender.hasPermission("kingdom.spawn.other") ) {
            kdc.getMessageManager().send(sender, "noPermissionCmd");
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdDefaultKingdomNotExist", args[0]);
            return;
        }

        KingdomAttribute attribute = kingdom.getAttribute("spawn");
        if ( attribute == null ) {
            kdc.getMessageManager().send(sender, "cmdSpawnOtherNotExists", kingdom.getName());
            return;
        }

        Location loc = LocationSerializer.deserialize(attribute.getValue());
        if ( loc == null ) {
            kdc.getMessageManager().send(sender, "cmdSpawnNotExists");
            return;
        }

        Teleporter.teleport((PlatformPlayer) sender, loc, kdc.getConfig().getTeleportDelay()).thenRun(() -> {
            DecimalFormat df = new DecimalFormat("#");
            String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

             kdc.getMessageManager().send(sender, "cmdSpawnSuccess", str);
        });
    }
}
