/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

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