package com.igufguf.kingdomcraft.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KingdomEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList(){
        return handlers;
    }
    public HandlerList getHandlers(){
        return handlers;
    }
}
