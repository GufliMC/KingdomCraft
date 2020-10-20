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

package com.guflan.kingdomcraft.api.command;

import com.guflan.kingdomcraft.api.entity.PlatformSender;

import java.util.List;

public interface CommandBase {

    List<String> getCommands();

    boolean isPlayerOnly();
    int getExpectedArguments();

    String getArgumentsHint();
    String getExplanationMessage();

    String[] getPermissions();

    void execute(PlatformSender sender, String[] args);

    default List<String> autocomplete(PlatformSender sender, String[] args) {
        return null;
    }

}
