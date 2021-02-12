package com.gufli.kingdomcraft.bukkit.gui;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.gui.InventoryClickType;
import com.gufli.kingdomcraft.api.gui.InventoryItemCallback;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;

public class InventoryBuilder {

    private final static MenuScheme[] SCHEMES = new MenuScheme[] {
            new MenuScheme("000000000", "000010000"), // 1 item
            new MenuScheme("000000000", "000101000"), // 2 items
            new MenuScheme("000000000", "001010100"), // 3 items
            new MenuScheme("000000000", "010101010"), // 4 items
            new MenuScheme("000000000", "001010100", "000101000"), // 5 items
            new MenuScheme("000000000", "001010100", "001010100"), // 6 items
            new MenuScheme("000000000", "010101010", "001010100"), // 7 items
            new MenuScheme("000000000", "010101010", "010101010"), // 8 items
            new MenuScheme("000000000", "001010100", "001010100", "001010100"), // 9 items
            new MenuScheme("000000000", "001010100", "010101010", "001010100") // 10 items
    };

    private final static MenuScheme[] ROW_SCHEMES = new MenuScheme[] {
            new MenuScheme("000010000"), // 1 item
            new MenuScheme("000101000"), // 2 items
            new MenuScheme("001010100"), // 3 items
            new MenuScheme("010101010"), // 4 items
            new MenuScheme("101010101"), // 5 items
            new MenuScheme("011101110"), // 6 items
            new MenuScheme("011111110"), // 7 items
            new MenuScheme("111101111"), // 8 items
            new MenuScheme("111111111"), // 9 items
    };

    //

    protected InventoryBuilder() {}

    public static InventoryBuilder create() {
        return new InventoryBuilder();
    }

    public static InventoryBuilder create(String title) {
        return new InventoryBuilder().withTitle(title);
    }

    //

    protected String title;

    protected final List<BukkitInventoryItem> items = new ArrayList<>();
    protected final Map<Integer, BukkitInventoryItem> hotbar = new HashMap<>();

    //

    public final InventoryBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public final InventoryBuilder withItem(ItemStack item) {
        items.add(new BukkitInventoryItem(item));
        return this;
    }

    public final InventoryBuilder withItems(ItemStack... items) {
        return withItems(Arrays.asList(items));
    }

    public final InventoryBuilder withItems(Iterable<ItemStack> items) {
        for ( ItemStack item : items ) {
            this.items.add(new BukkitInventoryItem(item));
        }
        return this;
    }

    public final InventoryBuilder withItem(ItemStack item, InventoryItemCallback cb) {
        items.add(new BukkitInventoryItem(item, cb));
        return this;
    }

    public final InventoryBuilder withItem(ItemStack item, BiConsumer<PlatformPlayer, InventoryClickType> cb) {
        items.add(new BukkitInventoryItem(item, (player, clickType) -> {
            cb.accept(player, clickType);
            return true;
        }));
        return this;
    }

    public final InventoryBuilder withHotbarItem(int slot, ItemStack item) {
        hotbar.put(slot, new BukkitInventoryItem(item));
        return this;
    }

    public final InventoryBuilder withHotbarItem(int slot, ItemStack item, InventoryItemCallback cb) {
        hotbar.put(slot, new BukkitInventoryItem(item, cb));
        return this;
    }

    public BukkitInventory buildRow() {
        if ( items.size() > 9 ) {
            throw new UnsupportedOperationException("More than 9 items is not supported.");
        }

        MenuScheme scheme = ROW_SCHEMES[items.size() - 1];
        return build(scheme);
    }

    public BukkitInventory build() {
        if ( items.size() > 10 ) {
            throw new UnsupportedOperationException("More than 10 items is not supported.");
        }

        MenuScheme scheme = SCHEMES[items.size() - 1];
        return build(scheme);
    }

    private BukkitInventory build(MenuScheme scheme) {
        int size = scheme.getRows();

        if ( !hotbar.isEmpty() ) {
            size += 2;
        } else if ( size != 1 ) {
            size += 1;
        }

        BukkitInventory menu = new BukkitInventory(size * 9, title);

        // fill items
        Iterator<Integer> slots = scheme.getSlots().iterator();
        for ( BukkitInventoryItem item : items ) {
            if ( !slots.hasNext() ) {
                break;
            }

            menu.setItem(slots.next(), item);
        }

        // fill hotbar
        for ( int slot : hotbar.keySet() ) {
            if ( slot < 0 || slot >= 9 ) {
                continue;
            }

            menu.setItem(((size-1) * 9) + slot, hotbar.get(slot));
        }

        return menu;
    }
    
}
