package com.gufli.kingdomcraft.bukkit.gui;

import com.gufli.kingdomcraft.api.gui.InventoryItemCallback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryBuilder {

    private final static InventoryScheme[] ROW_SCHEMES = new InventoryScheme[] {
            InventoryScheme.of("000010000"),
            InventoryScheme.of("000101000"),
            InventoryScheme.of("001010100"),
            InventoryScheme.of("010101010"),
            InventoryScheme.of("010111010"),
            InventoryScheme.of("011101110"),
            InventoryScheme.of("011111110"),
            InventoryScheme.of("111101111"),
            InventoryScheme.of("111111111")
    };

    public static InventoryBuilder create() {
        return new InventoryBuilder();
    }

    private String title;

    private ItemStack previousItem;
    private ItemStack nextItem;

    private final List<BukkitInventoryItem> items = new ArrayList<>();

    private final Map<Integer, BukkitInventoryItem> hotbar = new HashMap<>();

    private InventoryBuilder() {
        previousItem = ItemStackBuilder.of(Material.ARROW)
                .withName(ChatColor.GREEN + "Previous page")
                .build();

        nextItem = ItemStackBuilder.of(Material.ARROW)
                .withName(ChatColor.GREEN + "Next page")
                .build();
    }

    public InventoryBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public InventoryBuilder withItem(ItemStack item) {
        items.add(new BukkitInventoryItem(item));
        return this;
    }

    public InventoryBuilder withItems(ItemStack... items) {
        return withItems(Arrays.asList(items));
    }

    public InventoryBuilder withItems(Iterable<ItemStack> items) {
        for ( ItemStack item : items ) {
            this.items.add(new BukkitInventoryItem(item));
        }
        return this;
    }

    public InventoryBuilder withItem(ItemStack item, InventoryItemCallback cb) {
        items.add(new BukkitInventoryItem(item, cb));
        return this;
    }

    public InventoryBuilder withNextItem(ItemStack item) {
        this.nextItem = item;
        return this;
    }

    public InventoryBuilder withPreviousItem(ItemStack item) {
        this.previousItem = item;
        return this;
    }

    public InventoryBuilder withHotbarItem(int slot, ItemStack item) {
        hotbar.put(slot, new BukkitInventoryItem(item));
        return this;
    }

    public InventoryBuilder withHotbarItem(int slot, ItemStack item, InventoryItemCallback cb) {
        hotbar.put(slot, new BukkitInventoryItem(item, cb));
        return this;
    }

    public BukkitInventory build() {
        int size = (items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0);

        // no need for pagination
        if ( size <= 4 || (size <= 6 && hotbar.isEmpty()) ) {
            BukkitInventory inv = new BukkitInventory(size * 9 + (hotbar.isEmpty() ? 0 : 18), title);
            for ( int i = 0; i < items.size(); i++ ) {
                inv.setItem(i, items.get(i));
            }

            // fill hotbar
            hotbar.keySet().stream()
                    .filter(i -> i > 0 && i < 9)
                    .forEach(i -> inv.setItem(((size+1) * 9) + i, hotbar.get(i)));

            return inv;
        }

        return page(0);
    }

    public BukkitInventory buildS() {
        int size = (items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0);

        // no need for pagination
        if ( size <= 4 || (size <= 6 && hotbar.isEmpty()) ) {
            BukkitInventory inv = new BukkitInventory(size * 9 + (hotbar.isEmpty() ? 0 : 18), title);
            int rows = items.size() / 9 + (items.size() % 9 == 0 ? 0 : 1);

            for ( int row = 0; row < rows - 1; row++ ) {
                for ( int pos = 0; pos < 9; pos++ ) {
                    int index = (row * 9) + pos;
                    inv.setItem(index, items.get(index));
                }
            }

            if (items.size() > 0 ) {
                int startIndex = (rows - 1) * 9;
                int index = startIndex;
                for (int pos : ROW_SCHEMES[items.size() % 9 - 1].getAvailableSlots()) {
                    inv.setItem(startIndex + pos, items.get(index));
                    index++;
                }
            }

            // fill hotbar
            hotbar.keySet().stream()
                    .filter(i -> i > 0 && i < 9)
                    .forEach(i -> inv.setItem(((size+1) * 9) + i, hotbar.get(i)));

            return inv;
        }

        return build();
    }

    private BukkitInventory page(int page) {
        int size = (items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0);

        int pages = (size / 4) + (size % 4 > 0 ? 1 : 0);
        BukkitInventory inv = new BukkitInventory(54, title);

        // fill with items
        for ( int i = 0; i < Math.max(items.size(), 36); i++ ) {
            inv.setItem(i, items.get(i));
        }

        // fill hotbar
        for ( int i : hotbar.keySet() ) {
            if ( i >= 9 ) {
                continue;
            }

            inv.setItem(45 + i, hotbar.get(i));
        }

        if ( page > 0 ) {
            inv.setItem(47, new BukkitInventoryItem(previousItem, (player, clickType) -> {
                player.openInventory(page(page + 1));
                return true;
            }));
        }

        if ( page < pages - 1 ) {
            inv.setItem(51, new BukkitInventoryItem(nextItem, (player, clickType) -> {
                player.openInventory(page(page - 1));
                return true;
            }));
        }

        return inv;
    }

    public static class InventoryScheme {

        private final int[] mask;

        private InventoryScheme(int... rows) {
            mask = rows;
        }

        public static InventoryScheme of(int... rows) {
            return new InventoryScheme(rows);
        }

        public static InventoryScheme of(String... rows) {
            int[] result = new int[rows.length];
            for ( int i = 0; i < rows.length; i++ ) {
                result[i] = 0;
                for ( int pos = 0; pos < rows[i].length(); pos++ ) {
                    if ( rows[i].charAt(pos) == '1' ) {
                        result[i] ^= 1 << pos;
                    }
                }
            }
            return new InventoryScheme(result);
        }


        public int getSize() {
            return mask.length;
        }

        public boolean isMasked(int slot) {
            int row = slot / 9;
            int pos = slot % 9;

            if ( row >= mask.length ) {
                return false;
            }

            return ((mask[row] >> pos) & 1) == 1;
        }

        public List<Integer> getAvailableSlots() {
            List<Integer> slots = new ArrayList<>();
            for ( int slot = 0; slot < mask.length * 9; slot++ ) {
                if ( isMasked(slot) ) {
                    slots.add(slot);
                }
            }
            return slots;
        }
    }

}
