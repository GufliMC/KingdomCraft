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

package com.guflan.kingdomcraft.api.event;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.api.entity.Player;
import com.guflan.kingdomcraft.api.events.PlayerAttackPlayerEvent;

public interface EventManager {

    void addListener(EventListener listener);

    void removeListener(EventListener listener);

    // kingdoms

    void join(Player player);

    void quit(Player player);

    void kingdomJoin(User player);

    void kingdomLeave(User player, Kingdom oldKingdom);

    void kingdomCreate(Kingdom kingdom);

    void kingdomDelete(Kingdom kingdom);

    void playerAttack(PlayerAttackPlayerEvent event);

}
