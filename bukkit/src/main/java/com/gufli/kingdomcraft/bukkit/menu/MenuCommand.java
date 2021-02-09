package com.gufli.kingdomcraft.bukkit.menu;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

public class MenuCommand extends CommandBase {

    public MenuCommand(KingdomCraftImpl kdc) {
        super(kdc, "menu", 0, true);
        addCommand("panel");
        setExplanationMessage(kdc.getMessages().getMessage("cmdMenuExplanation"));
        setPermissions("kingdom.menu");
        KingdomMenu.kdc = kdc;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        KingdomMenu.open((PlatformPlayer) sender);
    }


}
