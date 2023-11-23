package com.gufli.kingdomcraft.bukkit.gui.skull;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class ModernSkullBuilder implements SkullBuilder {

    @Override
    public void withSkullOwner(ItemStack itemStack, OfflinePlayer owner) {
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(owner);
        itemStack.setItemMeta(meta);
    }

    @Override
    public void withSkullTexture(ItemStack itemStack, String texture) {
        try {
            withTexture(itemStack, new URL("https://textures.minecraft.net/texture/" + texture));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void withProfile(ItemStack itemStack, PlayerProfile profile) {
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwnerProfile(profile);
        itemStack.setItemMeta(meta);
    }

    private void withTexture(ItemStack itemStack, URL textureUrl) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), RandomStringUtils.randomAlphanumeric(16));
        profile.getTextures().setSkin(textureUrl);
        withProfile(itemStack, profile);
    }

}
