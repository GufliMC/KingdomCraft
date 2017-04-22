package com.igufguf.kingdomcraft.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import javax.annotation.Nonnull;
import java.util.List;

public class KingdomChatEvent extends KingdomEvent implements Cancellable {

    private boolean cancelled = false;

    private Player p;
    private String format;
    private String message;
    private List<Player> receivers;

    public KingdomChatEvent(@Nonnull Player p, @Nonnull String format, @Nonnull String message, @Nonnull List<Player> receivers) {
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
