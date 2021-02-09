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
