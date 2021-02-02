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

package com.gufli.kingdomcraft.api.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface User extends Model {

    UUID getUniqueId();

    String getName();

    Rank getRank();

    Kingdom getKingdom();

    void setKingdom(Kingdom kingdom);

    void setRank(Rank rank);

    List<KingdomInvite> getInvites();

    KingdomInvite getInvite(Kingdom kingdom);

    KingdomInvite addInvite(User sender);

    void clearInvites();

    UserChatChannel addChatChannel(String channel);

    UserChatChannel getChatChannel(String channel);

    Instant getCreatedAt();

    Instant getUpdatedAt();

}
