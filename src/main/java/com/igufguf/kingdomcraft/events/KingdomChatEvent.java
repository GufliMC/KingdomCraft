package com.igufguf.kingdomcraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.List;

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
public class KingdomChatEvent extends KingdomEvent implements Cancellable {

    private boolean cancelled = false;

    private Player p;
    private String format;
    private String message;
    private List<Player> receivers;

    public KingdomChatEvent(Player p, String format, String message, List<Player> receivers) {
        this.p = p;
        this.format = format;
        this.message = message;
        this.receivers = receivers;
    }

    public Player getPlayer() {
        return p;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String f) {
        this.format = f;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Player> getReceivers() {
        return receivers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
