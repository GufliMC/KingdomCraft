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

public class EditItemCommand extends CommandBase {

    public EditItemCommand(KingdomCraftImpl kdc) {
        super(kdc, "edit item", 0, true);
        setExplanationMessage("cmdItemExplanation");
        setPermissions("kingdom.edit.item");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        PlatformPlayer player = (PlatformPlayer) sender;

        Kingdom kingdom = player.getUser().getKingdom();
        if ( kingdom == null ) {
            kdc.getMessages().send(sender, "cmdErrorSenderNoKingdom");
            return;
        }

        ItemStack item = ((BukkitPlayer) player).getPlayer().getItemInHand();
        if ( item == null || item.getType() == Material.AIR ) {
            kdc.getMessages().send(sender, "cmdEditItemInvalid");
            return;
        }

        kingdom.setItem(new BukkitItem(item));
        kdc.saveAsync(kingdom);

        kdc.getMessages().send(sender, "cmdEdit", "item", item.getType().name());
    }
}
