package com.guflan.kingdomcraft.bukkit.permissions;

import com.guflan.kingdomcraft.bukkit.KingdomCraftBukkitPlugin;
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

        VaultPermissionsListener listener = new VaultPermissionsListener(plugin, permissionProvider.getProvider());
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getKdc().getEventManager().addListener(listener);
    }

}
