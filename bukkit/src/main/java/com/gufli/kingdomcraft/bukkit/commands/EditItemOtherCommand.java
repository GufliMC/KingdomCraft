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

package com.gufli.kingdomcraft.bukkit.commands;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.bukkit.entity.BukkitPlayer;
import com.gufli.kingdomcraft.bukkit.item.BukkitItem;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class EditItemOtherCommand extends CommandBase {

    public EditItemOtherCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit item", 1, true);
        setArgumentsHint("<kingdom>");
        setExplanationMessage(() -> kdc.getMessages().getMessage("cmdEditItemOtherExplanation"));
        setPermissions("kingdom.edit.item.other");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer sender, String[] args) {
        if ( args.length == 1 ) {
            return kdc.getKingdoms(true).stream().map(Kingdom::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        Kingdom kingdom = kdc.getKingdom(args[0]);
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorKingdomNotExist", args[0]);
            return;
        }

        ItemStack item = ((BukkitPlayer) sender).getPlayer().getItemInHand();
        if ( item == null || item.getType() == Material.AIR ) {
            kdc.getMessages().send(sender, "cmdEditItemInvalid");
            return;
        }

        kingdom.setItem(new BukkitItem(item));
        kdc.saveAsync(kingdom);

        kdc.getMessages().send(sender, "cmdEditOther", "item", kingdom.getName(), item.getType().name());
    }
}
