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

import com.guflan.kingdomcraft.api.entity.CommandSender;

import java.util.List;

public interface CommandManager {

    void registerCommand(CommandBase command);

    void unregisterCommand(CommandBase command);

    void execute(CommandSender sender, String[] args);

    List<String> autocomplete(CommandSender sender, String[] args);

}
