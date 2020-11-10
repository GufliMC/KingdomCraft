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

package com.guflan.kingdomcraft.common.commands.spawn;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;
import com.guflan.kingdomcraft.common.util.Teleporter;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnOtherCommand extends CommandBase {

    public SpawnOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "spawn", 1);
        setArgumentsHint("<kingdom>");
        setExplanationMessage("cmdSpawnOtherExplanation");
        setPermissions("kingdom.spawn.other");
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
        PlatformPlayer target;
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            target = kdc.getPlayer(args[0]);
            if ( target == null ) {
                kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
                return;
            }

            kingdom = kdc.getUser(target).getKingdom();
            if ( kingdom == null ) {
                kdc.getMessageManager().send(sender, "cmdErrorTargetNoKingdom", target.getName());
                return;
            }
        }  else if ( !(sender instanceof PlatformPlayer) ) {
            sender.sendMessage("This command cannot be executed in the console!");
            return;
        } else {
            target = (PlatformPlayer) sender;
        }

        PlatformLocation loc = kingdom.getSpawn();
        if ( loc == null ) {
            kdc.getMessageManager().send(sender, "cmdSpawnOtherNotExists", kingdom.getName());
            return;
        }

        Runnable r = () -> {
            DecimalFormat df = new DecimalFormat("#");
            String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

            kdc.getMessageManager().send(sender, "cmdSpawn", str);
        };

        if ( sender != target ) {
            Teleporter.teleport((PlatformPlayer) sender, loc, 0).thenRun(r);
        } else {
            Teleporter.teleport((PlatformPlayer) sender, loc).thenRun(r);
        }
    }
}
