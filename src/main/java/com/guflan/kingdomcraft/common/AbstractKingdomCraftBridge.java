package com.guflan.kingdomcraft.common;

import com.guflan.kingdomcraft.api.KingdomCraftBridge;
import com.guflan.kingdomcraft.api.chat.ChatManager;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.event.EventManager;
import com.guflan.kingdomcraft.api.managers.CommandManager;
import com.guflan.kingdomcraft.api.managers.KingdomManager;
import com.guflan.kingdomcraft.api.managers.MessageManager;
import com.guflan.kingdomcraft.api.managers.UserManager;
import com.guflan.kingdomcraft.api.placeholders.PlaceholderManager;
import com.guflan.kingdomcraft.api.scheduler.AbstractScheduler;
import com.guflan.kingdomcraft.common.chat.DefaultChatManager;
import com.guflan.kingdomcraft.common.command.DefaultCommandManager;
import com.guflan.kingdomcraft.common.event.DefaultEventManager;
import com.guflan.kingdomcraft.common.kingdom.DefaultKingdomManager;
import com.guflan.kingdomcraft.common.kingdom.DefaultUserManager;
import com.guflan.kingdomcraft.common.placeholders.DefaultPlaceholderManager;
import com.guflan.kingdomcraft.common.storage.Storage;

public abstract class AbstractKingdomCraftBridge implements KingdomCraftBridge {

    private DefaultKingdomManager kingdomManager;
    private DefaultUserManager userManager;
    private DefaultCommandManager commandManager;
    private DefaultEventManager eventManager;
    private DefaultChatManager chatManager;
    private DefaultPlaceholderManager placeholderManager;

    public AbstractKingdomCraftBridge(Storage storage) {
        this.commandManager = new DefaultCommandManager(this);
        this.userManager = new DefaultUserManager(this, storage);
        this.kingdomManager = new DefaultKingdomManager(this, storage);
        this.eventManager = new DefaultEventManager();
        this.chatManager = new DefaultChatManager(this);
        this.placeholderManager = new DefaultPlaceholderManager(this);
    }

    @Override
    public abstract AbstractScheduler getScheduler();

    @Override
    public abstract MessageManager getMessageManager();

    @Override
    public abstract void log(String msg);

    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public KingdomManager getKingdomManager() {
        return kingdomManager;
    }

    @Override
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public ChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    @Override
    public void join(User user) {
        this.userManager.join(user);
    }

    @Override
    public void quit(User user) {
        this.userManager.quit(user);
    }
}
