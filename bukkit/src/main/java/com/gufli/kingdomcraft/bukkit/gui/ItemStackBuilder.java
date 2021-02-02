package com.gufli.kingdomcraft.bukkit.gui;

import com.gufli.kingdomcraft.bukkit.reflection.Reflection;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemStackBuilder {

    private final ItemStack itemStack;

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(new ItemStack(material)).hideAttributes();
    }

    public static ItemStackBuilder of(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack).hideAttributes();
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public ItemStackBuilder transform(Consumer<ItemStack> is) {
        is.accept(this.itemStack);
        return this;
    }

    public ItemStackBuilder transformMeta(Consumer<ItemMeta> meta) {
        return transformMeta(ItemMeta.class, meta);
    }

    public <T extends ItemMeta> ItemStackBuilder transformMeta(Class<T> type, Consumer<T> meta) {
        T m = (T) this.itemStack.getItemMeta();
        if (m != null) {
            meta.accept(m);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackBuilder withName(String name) {
        return transformMeta(meta -> meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name)));
    }

    public ItemStackBuilder withType(Material material) {
        return transform(itemStack -> itemStack.setType(material));
    }

    public ItemStackBuilder withLore(String... lines) {
        return withLore(Arrays.asList(lines));
    }

    public ItemStackBuilder withLore(Iterable<String> lines) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            for (String line : lines) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder clearLore() {
        return transformMeta(meta -> meta.setLore(new ArrayList<>()));
    }

    public ItemStackBuilder withDurability(int durability) {
        return transform(itemStack -> itemStack.setDurability((short) durability));
    }

    public ItemStackBuilder withData(int data) {
        return withDurability(data);
    }

    public ItemStackBuilder withAmount(int amount) {
        return transform(itemStack -> itemStack.setAmount(amount));
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, int level) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, 1));
    }

    public ItemStackBuilder clearEnchantments() {
        return transform(itemStack -> itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment));
    }

    public ItemStackBuilder withItemFlag(ItemFlag... flags) {
        return transformMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemStackBuilder withoutItemFlag(ItemFlag... flags) {
        return transformMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemStackBuilder hideAttributes() {
        return withItemFlag(ItemFlag.values());
    }

    public ItemStackBuilder showAttributes() {
        return withoutItemFlag(ItemFlag.values());
    }

    public ItemStackBuilder withColor(Color color) {
        Material type = itemStack.getType();
        if (type != Material.LEATHER_BOOTS && type != Material.LEATHER_CHESTPLATE
                && type != Material.LEATHER_HELMET && type != Material.LEATHER_LEGGINGS) {
            return this;
        }

        return transformMeta(LeatherArmorMeta.class, meta -> {
            meta.setColor(color);
        });
    }

    public ItemStackBuilder apply(Consumer<ItemStackBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public ItemStackBuilder apply(boolean condition, Consumer<ItemStackBuilder> consumer) {
        if ( condition ) {
            consumer.accept(this);
        }
        return this;
    }

    public ItemStackBuilder apply(Supplier<Boolean> condition, Consumer<ItemStackBuilder> consumer) {
        return apply(condition.get(), consumer);
    }

    public ItemStackBuilder setPlayer(OfflinePlayer owner) {
        return transformMeta(SkullMeta.class, meta -> {
            meta.setOwner(owner.getName());
        });
    }

    // SKULLS

    private static Class<?> GameProfile;
    private static Method GameProfile_getProperties;
    private static Class<?> Property;
    private static Method Property_getName;
    private static Method PropertyMap_put;
    private static Field CraftMetaSkull_profile;

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
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public ItemStackBuilder withTexture(String texture) {
        try {
            UUID uuid = UUID.randomUUID();

            Object profile = GameProfile.getDeclaredConstructor(UUID.class, String.class).newInstance(uuid, uuid.toString().substring(0, 15));
            Object property = Property.getDeclaredConstructor(String.class, String.class).newInstance("textures", texture);
            Object properties = GameProfile_getProperties.invoke(profile);
            PropertyMap_put.invoke(properties, Property_getName.invoke(property), property);
            return withProfile(profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public ItemStackBuilder withTexture(UUID uuid) {
        try {
            Object profile = GameProfile.getDeclaredConstructor(UUID.class, String.class).newInstance(uuid, uuid.toString().substring(0, 15));
            return withProfile(profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    private ItemStackBuilder withProfile(Object profile) {
        try {
            SkullMeta meta = (SkullMeta) this.itemStack.getItemMeta();
            CraftMetaSkull_profile.set(meta, profile);
            this.itemStack.setItemMeta(meta);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    // BANNER

    public ItemStackBuilder withPattern(Pattern pattern) {
        return transformMeta(BannerMeta.class, meta ->
                meta.addPattern(pattern));
    }

    public ItemStackBuilder withPattern(DyeColor color, PatternType type) {
        return transformMeta(BannerMeta.class, meta ->
                meta.addPattern(new Pattern(color, type)));
    }

    public ItemStackBuilder setPattern(int layer, Pattern pattern) {
        return transformMeta(BannerMeta.class, meta ->
                meta.setPattern(layer, pattern));
    }

    public ItemStackBuilder setPattern(int layer, DyeColor color, PatternType type) {
        return transformMeta(BannerMeta.class, meta ->
                meta.setPattern(layer, new Pattern(color, type)));
    }

    public ItemStackBuilder withPatterns(Pattern... patterns) {
        return withPatterns(Arrays.asList(patterns));
    }

    public ItemStackBuilder withPatterns(List<Pattern> patterns) {
        return transformMeta(BannerMeta.class, meta ->
                meta.setPatterns(patterns));
    }

    // BOOK

    public ItemStackBuilder withAuthor(String author) {
        return transformMeta(BookMeta.class, meta ->
                meta.setAuthor(author));
    }

    public ItemStackBuilder withTitle(String title) {
        return transformMeta(BookMeta.class, meta ->
                meta.setTitle(title));
    }

    public ItemStackBuilder withPage(String contents) {
        return transformMeta(BookMeta.class, meta ->
                meta.addPage(contents));
    }

    public ItemStackBuilder withPages(String... contents) {
        return transformMeta(BookMeta.class, meta ->
                meta.setPages(contents));
    }

    public ItemStackBuilder withPages(List<String> contents) {
        return transformMeta(BookMeta.class, meta ->
                meta.setPages(contents));
    }

    // FIREWORK

    public ItemStackBuilder withPower(int power) {
        return transformMeta(FireworkMeta.class, meta ->
                meta.setPower(power));
    }

    public ItemStackBuilder withEffect(FireworkEffect effect) {
        return transformMeta(FireworkMeta.class, meta ->
                meta.addEffect(effect));
    }

    public ItemStackBuilder withEffects(FireworkEffect... effects) {
        return withEffects(Arrays.asList(effects));
    }

    public ItemStackBuilder withEffects(List<FireworkEffect> effects) {
        return transformMeta(FireworkMeta.class, meta -> {
            meta.clearEffects();
            meta.addEffects(effects);
        });
    }

    // POTION

    public ItemStackBuilder withPotionEffect(PotionEffect effect) {
        return transformMeta(PotionMeta.class, meta ->
                meta.addCustomEffect(effect, true));
    }

    public ItemStackBuilder withPotionEffects(PotionEffect... effects) {
        return withPotionEffects(Arrays.asList(effects));
    }

    public ItemStackBuilder withPotionEffects(List<PotionEffect> effects) {
        return transformMeta(PotionMeta.class, meta -> {
            for ( PotionEffect effect : effects ) {
                meta.addCustomEffect(effect, true);
            }
        });
    }

    public ItemStackBuilder withPotionEffect(PotionEffectType type) {
        return transformMeta(PotionMeta.class, meta ->
                meta.addCustomEffect(new PotionEffect(type, 1200, 0), true));
    }

    public ItemStackBuilder withPotionEffect(PotionEffectType type, int duration) {
        return transformMeta(PotionMeta.class, meta ->
                meta.addCustomEffect(new PotionEffect(type, duration, 0), true));
    }

    public ItemStackBuilder withPotionEffect(PotionEffectType type, int duration, int amplifier) {
        return transformMeta(PotionMeta.class, meta ->
                meta.addCustomEffect(new PotionEffect(type, duration, amplifier), true));
    }

    public ItemStackBuilder withPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient) {
        return transformMeta(PotionMeta.class, meta ->
                meta.addCustomEffect(new PotionEffect(type, duration, amplifier, ambient), true));
    }

    public ItemStackBuilder withPotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean particles) {
        return transformMeta(PotionMeta.class, meta ->
                meta.addCustomEffect(new PotionEffect(type, duration, amplifier, ambient, particles), true));
    }

}
