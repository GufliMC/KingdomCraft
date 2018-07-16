package com.igufguf.kingdomcraft.events;

import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRank;
import com.igufguf.kingdomcraft.objects.KingdomUser;

/**
 * Copyrighted 2018 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class KingdomLeaveEvent extends KingdomEvent {

    private KingdomObject oldkingdom;
    private KingdomRank oldRank;
    private KingdomUser user;

    public KingdomLeaveEvent(KingdomUser user, KingdomObject oldKingdom, KingdomRank oldRank) {
        this.user = user;
        this.oldkingdom = oldKingdom;
        this.oldRank = oldRank;
    }

    public KingdomUser getUser() {
        return user;
    }

    public KingdomRank getOldRank() {
        return oldRank;
    }

    public KingdomObject getOldkingdom() {
        return oldkingdom;
    }
}
