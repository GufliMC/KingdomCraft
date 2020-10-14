/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.placeholders;

import com.guflan.kingdomcraft.api.KingdomCraftHandler;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;

public class DefaultPlaceholderReplacer {

    public DefaultPlaceholderReplacer(KingdomCraftHandler kdc, PlaceholderManager pm) {

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
