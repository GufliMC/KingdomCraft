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

package com.gufli.kingdomcraft.api.event;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public interface EventListener {

    default void onLogin(PlatformPlayer player) {}

    default void onJoin(PlatformPlayer player) {}

    default void onQuit(PlatformPlayer player) {}

    default void onKingdomJoin(PlatformPlayer player) {}

    default void onKingdomLeave(PlatformPlayer player, Kingdom oldKingdom) {}

    default void onKingdomCreate(Kingdom kingdom) {}

    default void onKingdomDelete(Kingdom kingdom) {}

    default void onRankChange(PlatformPlayer player, Rank oldRank) {}

    default EventResult onPlayerAttack(PlatformPlayer player, PlatformPlayer attacker, EventResult result) {
        return result;
    }

    default void onReload() {}
}
