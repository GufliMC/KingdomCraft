/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.commands.admin;

import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBaseImpl;
import jdk.internal.joptsimple.internal.Strings;

import java.util.Arrays;

public class ChatCommand extends CommandBaseImpl {

    public ChatCommand(KingdomCraftImpl kdc) {
        super(kdc, "chat", -1, true);
        setArgumentsHint("<channel> <message>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdChatExplanation"));
        setPermissions("kingdom.chat");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        if ( args.length < 2 ) {
            kdc.getMessageManager().send(sender, "cmdDefaultInvalidUsage", "/k chat " + getArgumentsHint());
            return;
        }

        ChatChannel channel = kdc.getChatManager().getChatChannel(args[0]);
        if ( channel == null ) {
            kdc.getMessageManager().send(sender, "cmdChatNoChannel", args[0]);
            return;
        }

        String msg = Strings.join(Arrays.copyOfRange(args, 1, args.length), ", ");
        String stripped = kdc.getMessageManager().decolorify(kdc.getMessageManager().colorify(msg)).trim();
        if ( stripped.equals("") ) {
            kdc.getMessageManager().send(sender, "cmdChatEmptyMessage");
            return;
        }

        kdc.getChatDispatcher().send(player, channel, msg);
    }
}
