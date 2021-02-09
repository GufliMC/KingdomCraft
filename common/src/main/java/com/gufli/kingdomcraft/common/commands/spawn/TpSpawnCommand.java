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

public class TpSpawnCommand extends CommandBase {

    public TpSpawnCommand(KingdomCraftImpl kdc) {
        super(kdc, "tpspawn", 1);
        setArgumentsHint("<player>");
        setExplanationMessage("cmdTpSpawnExplanation");
        setPermissions("kingdom.tpspawn");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
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

        Kingdom kingdom = kdc.getUser(target).getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorTargetNoKingdom", target.getName());
            return;
        }

        PlatformLocation loc = kingdom.getSpawn();
        if ( loc == null ) {
            kdc.getMessages().send(sender, "cmdSpawnOtherNotExists", kingdom.getName());
            return;
        }

        target.teleport(loc);

        DecimalFormat df = new DecimalFormat("#");
        String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

        kdc.getMessages().send(sender, "cmdTpSpawn", str);
        kdc.getMessages().send(target, "cmdTpSpawnTarget", str);
    }
}
