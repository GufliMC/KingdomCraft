package com.gufli.kingdomcraft.bukkit.gui;

import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.gui.InventoryItemCallback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaginationBuilder {

    public static PaginationBuilder create() {
        return new PaginationBuilder();
    }

    public static PaginationBuilder create(String title) {
        return new PaginationBuilder().withTitle(title);
    }

    //

    private String title;

    private ItemStack previousItem;
    private ItemStack nextItem;

    private int itemAmount;
    private Function<Integer, BukkitInventoryItem> supplier;

    protected final Map<Integer, BukkitInventoryItem> hotbar = new HashMap<>();

    private PaginationBuilder() {
        previousItem = ItemStackBuilder.of(Material.ARROW)
                .withName(ChatColor.GREEN + "Previous page")
                .build();

        nextItem = ItemStackBuilder.of(Material.ARROW)
                .withName(ChatColor.GREEN + "Next page")
                .build();
    }

    //

    public final PaginationBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public PaginationBuilder withNextItem(ItemStack item) {
        this.nextItem = item;
        return this;
    }

    public PaginationBuilder withPreviousItem(ItemStack item) {
        this.previousItem = item;
        return this;
    }

    public PaginationBuilder withItems(int amount, Function<Integer, BukkitInventoryItem> supplier) {
        this.itemAmount = amount;
        this.supplier = supplier;
        return this;
    }

    public final PaginationBuilder withHotbarItem(int slot, ItemStack item) {
        hotbar.put(slot, new BukkitInventoryItem(item));
        return this;
    }

    public final PaginationBuilder withHotbarItem(int slot, ItemStack item, InventoryItemCallback cb) {
        hotbar.put(slot, new BukkitInventoryItem(item, cb));
        return this;
    }

    public BukkitInventory build() {
        BiConsumer<PlatformPlayer, Integer> openCallback = new BiConsumer<PlatformPlayer, Integer>() {
            @Override
            public void accept(PlatformPlayer player, Integer pageNumber) {
                player.openInventory(page(pageNumber, this));
            }
        };
        return build(openCallback);
    }

    public void buildAsync(PlatformPlayer player, Executor sync, Executor async) {
        if ( supplier == null ) {
            throw new IllegalStateException("Supplier may not be null.");
        }

        final BiConsumer<PlatformPlayer, Integer> openCallback = new BiConsumer<PlatformPlayer, Integer>() {
            @Override
            public void accept(PlatformPlayer player, Integer pageNumber) {
                async.execute(() -> {
                    BukkitInventory pageInv = page(pageNumber, this);
                    sync.execute(() -> player.openInventory(pageInv));
                });
            }
        };

        async.execute(() -> {
            BukkitInventory inv = build(openCallback);
            sync.execute(() -> player.openInventory(inv));
        });
    }

    private BukkitInventory build(BiConsumer<PlatformPlayer, Integer> openCallback) {
        if ( supplier == null ) {
            throw new IllegalStateException("Supplier may not be null.");
        }

        int size = (itemAmount / 9) + (itemAmount % 9 > 0 ? 1 : 0);

        if ( !hotbar.isEmpty() ) {
            size += 2;
        }

        // no need for pagination
        if ( size <= 6 ) {
            BukkitInventory menu = new BukkitInventory(size * 9, title);
            for ( int i = 0; i < itemAmount; i++ ) {
                menu.setItem(i, supplier.apply(i));
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

        return page(0, openCallback);
    }

    private BukkitInventory page(int page, BiConsumer<PlatformPlayer, Integer> openCallback) {
        int rows = (itemAmount / 9) + (itemAmount % 9 > 0 ? 1 : 0);
        int pages = (rows / 4) + (rows % 4 > 0 ? 1 : 0);

        BukkitInventory menu = new BukkitInventory(54, title);

        // fill with items
        int offset = page * 36;
        for ( int i = 0; i < Math.min(itemAmount - offset, 36); i++ ) {
            menu.setItem(i, supplier.apply(offset + i));
        }

        // fill hotbar
        for ( int slot : hotbar.keySet() ) {
            if ( slot >= 9 ) {
                continue;
            }

            menu.setItem(45 + slot, hotbar.get(slot));
        }

        if ( page > 0 ) {
            menu.setItem(47, new BukkitInventoryItem(previousItem, (player, clickType) -> {
                openCallback.accept(player, page - 1);
                return true;
            }));
        }

        if ( page < pages - 1 ) {
            menu.setItem(51, new BukkitInventoryItem(nextItem, (player, clickType) -> {
                openCallback.accept(player, page + 1);
                return true;
            }));
        }

        return menu;
    }

}
