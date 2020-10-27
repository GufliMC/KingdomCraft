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

package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.chat.ChatChannel;
import com.guflan.kingdomcraft.api.domain.User;
import com.guflan.kingdomcraft.api.domain.UserChatChannel;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.stream.Collectors;

public class ChatChannelCommand extends CommandBase {

    public ChatChannelCommand(KingdomCraftImpl kdc) {
        super(kdc, "chatchannel", 1, true);
        setArgumentsHint("<chatchannel>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdChatChannelExplanation"));
        setPermissions("kingdom.chatchannel");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        return kdc.getChatManager().getChatChannels().stream().filter(ChatChannel::isToggleable)
                .filter(c -> kdc.getChatManager().canAccess(sender, c))
                .map(ChatChannel::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        ChatChannel cc = kdc.getChatManager().getChatChannel(args[0]);
        if ( cc == null || (cc.isRestricted() && !sender.hasPermission(cc.getPermission()))) {
            kdc.getMessageManager().send(sender, "cmdChatChannelNotExist", args[0]);
            return;
        }

        if ( !cc.isToggleable() ) {
            kdc.getMessageManager().send(sender, "cmdChatChannelNoToggle", cc.getName());
            return;
        }

        User user = kdc.getUser((PlatformPlayer) sender);
        UserChatChannel ucc = user.getChatChannel(cc.getName());

        boolean isEnabled = ucc == null || ucc.isEnabled();

        if ( isEnabled ) {
            if ( ucc == null ) {
                ucc = user.addChatChannel(cc.getName());
            }
            ucc.setEnabled(false);
            kdc.getPlugin().getScheduler().executeAsync(ucc::save);
            kdc.getMessageManager().send(sender, "cmdChatChannelDisable", cc.getName());
        } else {
            ucc.setEnabled(true);
            kdc.getPlugin().getScheduler().executeAsync(ucc::save);
            kdc.getMessageManager().send(sender, "cmdChatChannelEnable", cc.getName());
        }
    }
}
