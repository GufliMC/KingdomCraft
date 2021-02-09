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

package com.gufli.kingdomcraft.api.events;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public class FriendlyFireCancelEvent extends PlayerEvent {

    private final PlatformPlayer attacker;

    private boolean allowed = false;

    public FriendlyFireCancelEvent(PlatformPlayer player, PlatformPlayer attacker) {
        super(player);
        this.attacker = attacker;
    }

    public PlatformPlayer getAttacker() {
        return attacker;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
