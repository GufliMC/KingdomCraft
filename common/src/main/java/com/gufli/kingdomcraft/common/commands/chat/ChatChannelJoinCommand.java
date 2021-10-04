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

package com.gufli.kingdomcraft.common.commands.chat;

import com.gufli.kingdomcraft.api.chat.ChatChannel;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.domain.UserChatChannel;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class ChatChannelJoinCommand extends CommandBase {

    public ChatChannelJoinCommand(KingdomCraftImpl kdc) {
        super(kdc, "chatchannel join", 1, true);
        addCommand("channel join");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdChatChannelJoinExplanation"));
        setPermissions("kingdom.chatchannel.join");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        return kdc.getChatManager().getChatChannels().stream().filter(ChatChannel::isToggleable)
                .filter(c -> kdc.getChatManager().canTalk(sender, c))
                .map(ChatChannel::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;
        ChatChannel cc = kdc.getChatManager().getChatChannel(args[0]);
        if ( cc == null || !kdc.getChatManager().canTalk(player, cc) || !cc.isEnabled()) {
            kdc.getMessages().send(sender, "cmdChatChannelNotExist", args[0]);
            return;
        }

        if ( !cc.isToggleable() ) {
            kdc.getMessages().send(sender, "cmdChatChannelNoToggle", cc.getName());
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        UserChatChannel ucc = user.getChatChannel(cc.getName());

        if ( ucc == null || ucc.isEnabled() ) {
            kdc.getMessages().send(sender, "cmdChatChannelJoinAlready", cc.getName());
            return;
        }

        ucc.setEnabled(true);
        kdc.saveAsync(ucc);

        kdc.getMessages().send(sender, "cmdChatChannelJoin", cc.getName());
    }
}
