package com.gufli.kingdomcraft.bukkit.panel;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

public class PanelCommand extends CommandBase {

    public PanelCommand(KingdomCraftImpl kdc) {
        super(kdc, "panel", 0, true);
        addCommand("menu");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdPanelExplanation"));
        setPermissions("kingdom.panel");

        MainPanel.kdc = kdc;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        MainPanel.open((PlatformPlayer) sender);
    }


}
