package com.igufguf.kingdomcraft.common.placeholders;

import com.igufguf.kingdomcraft.api.KingdomCraftPlugin;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderManager;

public class DefaultPlaceholderReplacer {

    public DefaultPlaceholderReplacer(KingdomCraftPlugin plugin, PlaceholderManager pm) {
        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? plugin.translateColors(player.getKingdom().getDisplay()) : "",
                "kingdom");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? player.getKingdom().getName() : "",
                "kingdom_name");

        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? plugin.translateColors(player.getRank().getDisplay()) : "",
                "rank");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? player.getRank().getName() : "",
                "rank_name");

        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? plugin.translateColors(player.getKingdom().getPrefix()) : "",
                "kingdom_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getKingdom() != null ? plugin.translateColors(player.getKingdom().getSuffix()) : "",
                "kingdom_suffix");

        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? plugin.translateColors(player.getRank().getPrefix()) : "",
                "rank_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> player.getRank() != null ? plugin.translateColors(player.getRank().getSuffix()) : "",
                "rank_suffix");

    }

}
