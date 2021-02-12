package com.gufli.kingdomcraft.bukkit.gui;

import java.util.ArrayList;
import java.util.List;

public class MenuScheme {

    private static final int width = 9;
    private final String[] mask;

    public MenuScheme(String... rows) {
        for (int i = 0; i < rows.length; i++) {
            if ( rows[i].length() != width ) {
                throw new IllegalStateException("Invalid row " + i + ", width must be " + width);
            }
        }

        mask = rows;
    }

    public int getRows() {
        return mask.length;
    }

    public boolean isMasked(int slot) {
        int row = slot / width;
        int pos = slot % width;

        if (row >= mask.length) {
            return false;
        }

        return mask[row].charAt(pos) == '1';
    }

    public List<Integer> getSlots() {
        List<Integer> slots = new ArrayList<>();
        for ( int row = 0; row < mask.length; row++ ) {
            for (int pos = 0; pos < width; pos++ ) {
                if ( mask[row].charAt(pos) == '1' ) {
                    slots.add(row * 9 + pos);
                }
            }
        }
        return slots;
    }
}