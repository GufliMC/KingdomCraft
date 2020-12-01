/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.placeholders;

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.placeholders.PlaceholderManager;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;

public class DefaultPlaceholderReplacer {

    public DefaultPlaceholderReplacer(KingdomCraftImpl kdc, PlaceholderManager pm) {

        pm.addPlaceholderReplacer((player, placeholder) -> player.getName(), "player", "username");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            if ( user == null ) {
                return "";
            }
            String display;
            if ( user.getKingdom() == null ) {
                display = kdc.getConfig().getNoKingdomDisplay();
            } else {
                display = user.getKingdom() != null ? user.getKingdom().getDisplay() : "";
            }
            return kdc.getMessageManager().colorify(display);
        }, "kingdom");
        pm.addPlaceholderReplacer((player, placeholder) -> {
           User user = kdc.getUser(player);
           return user != null && user.getKingdom() != null ? user.getKingdom().getName() : "";
        }, "kingdom_name");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user != null && user.getRank() != null ? kdc.getMessageManager().colorify(user.getRank().getDisplay()) : "";
        }, "rank");
        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user != null && user.getRank() != null ? user.getRank().getName() : "";
        }, "rank_name");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            if ( user == null ) {
                return "";
            }
            String prefix;
            if ( user.getKingdom() == null ) {
                prefix = kdc.getConfig().getNoKingdomPrefix();
            } else {
                prefix = user.getKingdom().getPrefix();
            }
            return kdc.getMessageManager().colorify(prefix);
        }, "kingdom_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            if ( user == null ) {
                return "";
            }
            String suffix;
            if ( user.getKingdom() == null ) {
                suffix = kdc.getConfig().getNoKingdomSuffix();
            } else {
                suffix = user.getKingdom().getSuffix();
            }
            return kdc.getMessageManager().colorify(suffix);
        }, "kingdom_suffix");

        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user != null && user.getRank() != null ? kdc.getMessageManager().colorify(user.getRank().getPrefix()) : "";
        }, "rank_prefix");
        pm.addPlaceholderReplacer((player, placeholder) -> {
            User user = kdc.getUser(player);
            return user != null && user.getRank() != null ? kdc.getMessageManager().colorify(user.getRank().getSuffix()) : "";
        }, "rank_suffix");

    }

}
