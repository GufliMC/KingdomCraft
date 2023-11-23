package com.gufli.kingdomcraft.bukkit.gui.skull;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public interface SkullBuilder {

    void withSkullOwner(ItemStack itemStack, OfflinePlayer owner);

    void withSkullTexture(ItemStack itemStack, String texture);

}
