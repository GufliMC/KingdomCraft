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

package com.gufli.kingdomcraft.bukkit.permissions;

import com.gufli.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermissionHandler {

    public VaultPermissionHandler(KingdomCraftBukkitPlugin plugin) {
        if ( !plugin.getServer().getPluginManager().isPluginEnabled("Vault") ) {
            return;
        }

        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider == null) {
            return;
        }

        VaultPermissionListener listener = new VaultPermissionListener(plugin, permissionProvider.getProvider());
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

}
