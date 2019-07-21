package com.igufguf.kingdomcraft.api.events;

import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Joris on 19/08/2018 in project KingdomCraft.
 */
public class AsyncKingdomUserSaveEvent extends KingdomEvent {

    private KingdomUser user;
    private ConfigurationSection configurationSection;

    public AsyncKingdomUserSaveEvent(KingdomUser user, ConfigurationSection configurationSection, boolean async) {
        super(async);
        this.user = user;
        this.configurationSection = configurationSection;
    }

    public KingdomUser getUser() {
        return user;
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }

}
