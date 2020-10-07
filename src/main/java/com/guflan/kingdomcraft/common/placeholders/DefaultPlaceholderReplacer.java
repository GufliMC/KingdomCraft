package com.guflan.kingdomcraft.common.placeholders;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.messages.MessageManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

public class DefaultPlaceholderReplacer {

    public DefaultPlaceholderReplacer(KingdomCraft kdc, PlaceholderManager pm) {
        MessageManager mm = kdc.getMessageManager();

        pm.addPlaceholderReplacer((player, placeholder) -> {
                    User user = kdc.getUser(player);
                    return user.getKingdom() != null ? mm.colorify(user.getKingdom().getDisplay()) : "";
                }, "kingdom");
        pm.addPlaceholderReplacer((player, placeholder) -> {
                   User user = kdc.getUser(player);
                   return user.getKingdom() != null ? user.getKingdom().getName() : "";
                }, "kingdom_name");

        pm.addPlaceholderReplacer((player, placeholder) -> {
                    User user = kdc.getUser(player);
                    return user.getRank() != null ? mm.colorify(user.getRank().getDisplay()) : "";
                }, "rank");
        pm.addPlaceholderReplacer((player, placeholder) -> {
                    User user = kdc.getUser(player);
                    return user.getRank() != null ? user.getRank().getName() : "";
                }, "rank_name");

        pm.addPlaceholderReplacer((player, placeholder) -> {
                    User user = kdc.getUser(player);
                    return user.getKingdom() != null ? mm.colorify(user.getKingdom().getPrefix()) : "";
                }, "kingdom_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> {
                    User user = kdc.getUser(player);
                    return user.getKingdom() != null ? mm.colorify(user.getKingdom().getSuffix()) : "";
                }, "kingdom_suffix");

        pm.addPlaceholderReplacer((player, placeholder) -> {
                    User user = kdc.getUser(player);
                    return user.getRank() != null ? mm.colorify(user.getRank().getPrefix()) : "";
                }, "rank_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> {
                    User user = kdc.getUser(player);
                    return user.getRank() != null ? mm.colorify(user.getRank().getSuffix()) : "";
                }, "rank_suffix");

    }

}
