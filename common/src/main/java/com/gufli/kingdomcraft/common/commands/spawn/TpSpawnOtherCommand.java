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

package com.gufli.kingdomcraft.common.commands.spawn;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformLocation;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class TpSpawnOtherCommand extends CommandBase {

    public TpSpawnOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "tpspawn", 2);
        setArgumentsHint("<player> <kingdom>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdTpSpawnOtherExplanation"));
        setPermissions("kingdom.tpspawn.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getOnlinePlayers().stream().map(PlatformPlayer::getName).collect(Collectors.toList());
        }
        if ( args.length == 2 ) {
            return kdc.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer target = kdc.getPlayer(args[0]);
        if ( target == null ) {
            kdc.getMessages().send(sender, "cmdErrorNotOnline", args[0]);
            return;
        }

        Kingdom kingdom = kdc.getKingdom(args[1]);
        if ( kingdom == null || kingdom.isTemplate() ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[1]);
            return;
        }

        PlatformLocation loc = kingdom.getSpawn();
        if ( loc == null ) {
            kdc.getMessages().send(sender, "cmdSpawnOtherNotExists", kingdom.getName());
            return;
        }

        target.teleport(loc);

        kdc.getMessages().send(sender, "cmdTpSpawn", target.getName());
        kdc.getMessages().send(target, "cmdTpSpawnTarget", kingdom.getName());
    }
}
