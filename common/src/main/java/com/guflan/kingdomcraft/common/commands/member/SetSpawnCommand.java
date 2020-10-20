package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;

import java.text.DecimalFormat;

public class SetSpawnCommand extends CommandBaseImpl {

    public SetSpawnCommand(KingdomCraftImpl kdc) {
        super(kdc, "setspawn", 0, true);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdSetSpawnExplanation"));
        setPermissions("kingdom.setspawn");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        User user = kdc.getUser((PlatformPlayer) sender);
        Kingdom kingdom = user.getKingdom();
        if (kingdom == null) {
            kdc.getMessageManager().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        PlatformLocation loc = ((PlatformPlayer) sender).getLocation();
        kingdom.setSpawn(loc);

        // async saving
        kdc.getPlugin().getScheduler().executeAsync(kingdom::save);

        DecimalFormat df = new DecimalFormat("#");
        String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

        kdc.getMessageManager().send(sender, "cmdSetSpawnSuccess", str);
    }
}
