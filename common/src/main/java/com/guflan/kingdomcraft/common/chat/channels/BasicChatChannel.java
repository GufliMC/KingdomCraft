/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.chat.channels;

import com.guflan.kingdomcraft.api.chat.ChatChannel;

public class BasicChatChannel implements ChatChannel {

    private final String name;

    private String prefix;
    private String format;

    private boolean restricted;
    private boolean toggleable;

    private int range;

    public BasicChatChannel(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public boolean isRestricted() {
        return restricted;
    }

    @Override
    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
    }

    @Override
    public String getPermission() {
        return "kingdom.chat.channel." + name;
    }

    @Override
    public boolean isToggleable() {
        return toggleable;
    }

    @Override
    public void setToggleable(boolean toggleable) {
        this.toggleable = toggleable;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public void setRange(int range) {
        this.range = range;
    }

}
