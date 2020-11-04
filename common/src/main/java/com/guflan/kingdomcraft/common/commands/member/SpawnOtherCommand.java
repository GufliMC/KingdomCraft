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

package com.guflan.kingdomcraft.common.commands.member;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.entity.PlatformLocation;
import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;
import com.guflan.kingdomcraft.common.util.Teleporter;

import java.text.DecimalFormat;

public class SpawnOtherCommand extends CommandBase {

    public SpawnOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "spawn", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(kdc.getMessageManager().getMessage("cmdSpawnOtherExplanation"));
        setPermissions("kingdom.spawn.other");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessageManager().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        PlatformLocation loc = kingdom.getSpawn();
        if ( loc == null ) {
            kdc.getMessageManager().send(sender, "cmdSpawnOtherNotExists", kingdom.getName());
            return;
        }

        Teleporter.teleport((PlatformPlayer) sender, loc).thenRun(() -> {
            DecimalFormat df = new DecimalFormat("#");
            String str = df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ());

             kdc.getMessageManager().send(sender, "cmdSpawn", str);
        });
    }
}
