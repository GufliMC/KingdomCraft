package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import com.guflan.kingdomcraft.common.util.Teleporter;

import java.text.DecimalFormat;

public class SpawnOtherCommand extends CommandBaseImpl {

    public SpawnOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "spawn", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdSpawnOtherExplanation"));
        setPermissions("kingdom.spawn.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        PlatformLocation loc = kingdom.getSpawn();
        if ( loc == null ) {
            kdc.getMessageManager().send(sender, "cmdSpawnOtherNotExists", kingdom.getName());
            return;
        }

        Teleporter.teleport((PlatformPlayer) sender, loc, kdc.getConfig().getTeleportDelay()).thenRun(() -> {
            DecimalFormat df = new DecimalFormat("#");
            String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

             kdc.getMessageManager().send(sender, "cmdSpawnSuccess", str);
        });
    }
}
