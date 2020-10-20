package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

import java.text.DecimalFormat;

public class SetSpawnOtherCommand extends CommandBaseImpl {

    public SetSpawnOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "setspawn", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdSetSpawnOtherExplanation"));
        setPermissions("kingdom.setspawn.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if (kingdom == null) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        PlatformLocation loc = ((PlatformPlayer) sender).getLocation();
        kingdom.setSpawn(loc);

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(kingdom::save);

        DecimalFormat df = new DecimalFormat("#");
        String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

        kdc.getMessageManager().send(sender, "cmdSetSpawnOtherSuccess", kingdom.getName(), str);
    }
}
