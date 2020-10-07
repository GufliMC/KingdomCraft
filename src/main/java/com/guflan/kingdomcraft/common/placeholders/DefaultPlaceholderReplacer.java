package com.guflan.kingdomcraft.common.placeholders;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

public class DefaultPlaceholderReplacer {

    public DefaultPlaceholderReplacer(KingdomCraft bridge, PlaceholderManager pm) {
        MessageManager mm = bridge.getMessageManager();

        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? mm.colorify(player.getKingdom().getDisplay()) : "",
                "kingdom");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? player.getKingdom().getName() : "",
                "kingdom_name");

        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? mm.colorify(player.getRank().getDisplay()) : "",
                "rank");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? player.getRank().getName() : "",
                "rank_name");

        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? mm.colorify(player.getKingdom().getPrefix()) : "",
                "kingdom_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? mm.colorify(player.getKingdom().getSuffix()) : "",
                "kingdom_suffix");

        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? mm.colorify(player.getRank().getPrefix()) : "",
                "rank_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? mm.colorify(player.getRank().getSuffix()) : "",
                "rank_suffix");

    }

}
