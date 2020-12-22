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
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatCommand extends CommandBase {

    public ChatCommand(KingdomCraftImpl kdc) {
        super(kdc, "chat", -1, true);
        addCommand("c");
        setArgumentsHint("<channel> <message>");
        setExplanationMessage("cmdChatExplanation");
        setPermissions("kingdom.chat", "kingdom.chat.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getChatManager().getChatChannels().stream()
                    .filter(c -> player.hasPermission("kingdom.chat.other") || kdc.getChatManager().canTalk(player, c))
                    .map(ChatChannel::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        if ( args.length < 2 ) {
            kdc.getMessageManager().send(sender, "cmdErrorInvalidUsage", "/k chat " + getArgumentsHint());
            return;
        }

        ChatChannel channel = kdc.getChatManager().getChatChannel(args[0]);
        if ( channel == null ) {
            kdc.getMessageManager().send(sender, "cmdChatNoChannel", args[0]);
            return;
        }

        if ( !kdc.getChatManager().canTalk(player, channel)
                && !player.hasPermission("kingdom.chat.other") ) {
            kdc.getMessageManager().send(sender, "cmdErrorNoPermission");
            return;
        }

        String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String stripped = kdc.getMessageManager().decolorify(kdc.getMessageManager().colorify(msg)).trim();
        if ( stripped.equals("") ) {
            kdc.getMessageManager().send(sender, "cmdChatEmptyMessage");
            return;
        }

        kdc.getChatDispatcher().send(player, channel, msg);
    }
}
