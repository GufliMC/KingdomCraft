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

package com.gufli.kingdomcraft.bukkit.menu;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class InfoCommand extends CommandBase {

    public InfoCommand(KingdomCraftImpl kdc) {
        super(kdc, "info", -1, true);
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdInfoExplanation"));
        setPermissions("kingdom.menu");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        List<String> result = kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
        result.addAll(kdc.getOnlinePlayers().stream().filter(p -> p != player)
                        .map(PlatformPlayer::getName).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;
        User user = kdc.getUser(player);

        if ( args.length == 0 || args[0].equalsIgnoreCase(player.getName()) ) {
            MainMenu.openPlayerInfo(player, user);
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom != null ) {
            MainMenu.openKingdomInfo(player, kingdom);
            return;
        }

        kdc.getPlugin().getScheduler().executeAsync(() -> {
            try {
                User target = kdc.getUser(args[0]).get();
                if ( target == null ) {
                    kdc.getMessageManager().send(player, "cmdErrorPlayerNotExist", args[0]);
                    return;
                }

                kdc.getPlugin().getScheduler().executeSync(() -> MainMenu.openPlayerInfo(player, target));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
