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

import com.gufli.kingdomcraft.api.chat.ChatChannel;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

import java.util.List;

public class PlayerChatEvent extends PlayerEvent {

    private final ChatChannel chatChannel;

    private boolean cancelled = false;
    private String message;
    private List<PlatformPlayer> receivers;


    public PlayerChatEvent(PlatformPlayer player, ChatChannel channel, String message, List<PlatformPlayer> receivers) {
        super(player);
        this.chatChannel = channel;
        this.message = message;
        this.receivers = receivers;
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<PlatformPlayer> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<PlatformPlayer> receivers) {
        this.receivers = receivers;
    }
}
