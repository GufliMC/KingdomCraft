package com.gufli.kingdomcraft.bukkit.gui.skull;

import com.gufli.kingdomcraft.bukkit.reflection.Reflection;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class LegacySkullBuilder implements SkullBuilder {

    private static Class<?> GameProfile;
    private static Method GameProfile_getProperties;
    private static Class<?> Property;
    private static Method Property_getName;
    private static Method PropertyMap_put;
    private static Field CraftMetaSkull_profile;
    private static Method CraftMetaSkull_setOwningPlayer;

    static {

        try {
            GameProfile = Class.forName("com.mojang.authlib.GameProfile");
            GameProfile_getProperties = GameProfile.getDeclaredMethod("getProperties");

            Property = Class.forName("com.mojang.authlib.properties.Property");
            Property_getName = Property.getMethod("getName");

            Class<?> PropertyMap = Class.forName("com.mojang.authlib.properties.PropertyMap");
            PropertyMap_put = PropertyMap.getMethod("put", Object.class, Object.class);

            Class<?> CraftMetaSkull = Reflection.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftMetaSkull");
            CraftMetaSkull_profile = CraftMetaSkull.getDeclaredField("profile");
            CraftMetaSkull_profile.setAccessible(true);

            // Optional
            try {
                CraftMetaSkull_setOwningPlayer = CraftMetaSkull.getMethod("setOwningPlayer", OfflinePlayer.class);
            } catch (NoSuchMethodException ignored) {}

        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void withSkullOwner(ItemStack itemStack, OfflinePlayer owner) {
        if ( owner == null ) {
            return;
        }

        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        if ( CraftMetaSkull_setOwningPlayer != null ) {
            try {
                CraftMetaSkull_setOwningPlayer.invoke(meta, owner);
                return;
            } catch (IllegalAccessException | InvocationTargetException ignored) {}
        }

        // Fallback to deprecated method
        meta.setOwner(owner.getName());
        itemStack.setItemMeta(meta);
    }

    @Override
    public void withSkullTexture(ItemStack itemStack, String texture) {
        try {
            UUID uuid = UUID.randomUUID();
            Object profile = GameProfile.getDeclaredConstructor(UUID.class, String.class).newInstance(uuid, uuid.toString().substring(0, 15));
            Object property = Property.getDeclaredConstructor(String.class, String.class).newInstance("textures", texture);
            Object properties = GameProfile_getProperties.invoke(profile);
            PropertyMap_put.invoke(properties, Property_getName.invoke(property), property);
            withSkullProfile(itemStack, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

//    public void withSkullTexture(ItemStack itemStack, UUID uuid) {
//        try {
//            Object profile = GameProfile.getDeclaredConstructor(UUID.class, String.class).newInstance(uuid, uuid.toString().substring(0, 15));
//            withSkullProfile(itemStack, profile);
//        } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException ex) {
//            ex.printStackTrace();
//        }
//    }

    private void withSkullProfile(ItemStack itemStack, Object profile) {
        try {
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            CraftMetaSkull_profile.set(meta, profile);
            itemStack.setItemMeta(meta);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
