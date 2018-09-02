package com.igufguf.kingdomcraft.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
public class KingdomEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList(){
        return handlers;
    }
    public HandlerList getHandlers(){
        return handlers;
    }
}
