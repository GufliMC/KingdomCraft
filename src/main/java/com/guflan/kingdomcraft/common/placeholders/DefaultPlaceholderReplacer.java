package com.guflan.kingdomcraft.common.placeholders;

import com.guflan.kingdomcraft.api.KingdomCraft;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

public class DefaultPlaceholderReplacer {

    public DefaultPlaceholderReplacer(KingdomCraft kdc, PlaceholderManager pm) {

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user.getKingdom() != null ? kdc.getMessageManager().colorify(user.getKingdom().getDisplay()) : "";
        }, "kingdom");
        pm.addPlaceholderReplacer((player, placeholder) -> {
           User user = kdc.getUser(player);
           return user.getKingdom() != null ? user.getKingdom().getName() : "";
        }, "kingdom_name");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user.getRank() != null ? kdc.getMessageManager().colorify(user.getRank().getDisplay()) : "";
        }, "rank");
        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user.getRank() != null ? user.getRank().getName() : "";
        }, "rank_name");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user.getKingdom() != null ? kdc.getMessageManager().colorify(user.getKingdom().getPrefix()) : "";
        }, "kingdom_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user.getKingdom() != null ? kdc.getMessageManager().colorify(user.getKingdom().getSuffix()) : "";
        }, "kingdom_suffix");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user.getRank() != null ? kdc.getMessageManager().colorify(user.getRank().getPrefix()) : "";
        }, "rank_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user.getRank() != null ? kdc.getMessageManager().colorify(user.getRank().getSuffix()) : "";
        }, "rank_suffix");

    }

}
