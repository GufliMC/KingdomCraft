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

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.placeholders.PlaceholderManager;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;

public class DefaultPlaceholderReplacer {

    public DefaultPlaceholderReplacer(KingdomCraftImpl kdc, PlaceholderManager pm) {

        pm.addPlaceholderReplacer((user, placeholder) -> user.getName(), "user", "username");

        pm.addPlaceholderReplacer((user, placeholder) -> {
            if (user.getKingdom() != null) {
                return kdc.getMessages().colorify(user.getKingdom().getDisplay());
            }

            PlatformPlayer pp = kdc.getPlayer(user);
            if (pp != null && pp.hasPermission("kingdom.placeholders.empty-no-kingdom")) {
                return null;
            }

            return kdc.getMessages().colorify(kdc.getConfig().getNoKingdomDisplay());
        }, "kingdom");
        pm.addPlaceholderReplacer((user, placeholder) -> {
            return user.getKingdom() != null ? user.getKingdom().getName() : "";
        }, "kingdom_name");

        pm.addPlaceholderReplacer((user, placeholder) -> {
            return user.getRank() != null ? kdc.getMessages().colorify(user.getRank().getDisplay()) : "";
        }, "rank");
        pm.addPlaceholderReplacer((user, placeholder) -> {
            return user.getRank() != null ? user.getRank().getName() : "";
        }, "rank_name");

        pm.addPlaceholderReplacer((user, placeholder) -> {
            if (user.getKingdom() != null) {
                return kdc.getMessages().colorify(user.getKingdom().getPrefix());
            }

            PlatformPlayer pp = kdc.getPlayer(user);
            if (pp != null && pp.hasPermission("kingdom.placeholders.empty-no-kingdom")) {
                return null;
            }

            return kdc.getMessages().colorify(kdc.getConfig().getNoKingdomPrefix());
        }, "kingdom_prefix");
        pm.addPlaceholderReplacer((user, placeholder) -> {
            if (user.getKingdom() != null) {
                return kdc.getMessages().colorify(user.getKingdom().getSuffix());
            }

            PlatformPlayer pp = kdc.getPlayer(user);
            if (pp != null && pp.hasPermission("kingdom.placeholders.empty-no-kingdom")) {
                return null;
            }

            return kdc.getMessages().colorify(kdc.getConfig().getNoKingdomSuffix());
        }, "kingdom_suffix");

        pm.addPlaceholderReplacer((user, placeholder) -> {
            return user.getRank() != null ? kdc.getMessages().colorify(user.getRank().getPrefix()) : "";
        }, "rank_prefix");
        pm.addPlaceholderReplacer((user, placeholder) -> {
            return user.getRank() != null ? kdc.getMessages().colorify(user.getRank().getSuffix()) : "";
        }, "rank_suffix");

        pm.addPlaceholderReplacer((user, placeholder) -> {
            if (user.getKingdom() == null) {
                return "";
            }
            return user.getKingdom().getMemberCount() + "";
        }, "kingdom_member_count");
        pm.addPlaceholderReplacer((user, placeholder) -> {
            if (user.getKingdom() == null) {
                return "";
            }
            return kdc.getOnlineUsers().stream().filter(u -> u.getKingdom() == user.getKingdom()).count() + "";
        }, "kingdom_online_member_count");

        pm.addPlaceholderReplacer((user, placeholder) -> {
            if (user.getKingdom() == null || user.getRank() == null) {
                return "";
            }
            return user.getRank().getMemberCount() + "";
        }, "rank_member_count");
        pm.addPlaceholderReplacer((user, placeholder) -> {
            if (user.getKingdom() == null || user.getRank() == null) {
                return "";
            }
            return kdc.getOnlineUsers().stream().filter(u -> u.getRank() == user.getRank()).count() + "";
        }, "rank_online_member_count");


    }

}
