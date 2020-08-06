package com.igufguf.kingdomcraft.common.commands.member;

import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.domain.DKingdomInvite;
import com.igufguf.kingdomcraft.common.domain.DPlayer;
import com.igufguf.kingdomcraft.common.commands.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyrighted 2020 iGufGuf
 * <p>
 * This file is part of KingdomCraft.
 * <p>
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 **/
public class InviteCommand extends CommandBase {

    public InviteCommand(KingdomCraft kingdomCraft) {
        super(kingdomCraft, "invite");
    }

    @Override
    public List<String> autocomplete(org.bukkit.entity.Player sender, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sendInvalidUsage(sender);
            return;
        }

        DPlayer player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);
        if (player.getKingdom() == null) {
            kingdomCraft.messageHandler.send(sender, "cmdDefaultSenderNoKingdom");
            return;
        }

        org.bukkit.entity.Player bplayer = Bukkit.getPlayer(args[0]);
        if ( bplayer == null ) {
            kingdomCraft.messageHandler.send(sender, "cmdDefaultNotOnline");
            return;
        }

        DPlayer target = kingdomCraft.playerHandler.getPlayer(bplayer);
        if ( target.getKingdom() == player.getKingdom() ) {
            kingdomCraft.messageHandler.send(sender, "cmdInviteAlready", target.getName());
            return;
        }

        DKingdomInvite invite = kingdomCraft.kingdomHandler.getInvite(target, player.getKingdom());
        if ( invite != null ) {
            kingdomCraft.messageHandler.send(sender, "cmdInviteAlready", target.getName());
            return;
        }

        kingdomCraft.kingdomHandler.addInvite(player, target);
        kingdomCraft.messageHandler.send(bplayer, "cmdInviteTarget", player.getKingdom().getName());
    }

}
