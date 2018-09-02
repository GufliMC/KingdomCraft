package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.models.commands.CommandBase;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public interface KingdomCommandHandler {

    void register(CommandBase base);

    void unregister(CommandBase base);

    CommandBase getByCommand(String cmd);

}
