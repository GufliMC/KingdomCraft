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

package com.gufli.kingdomcraft.api.placeholders;

import com.gufli.kingdomcraft.api.domain.User;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;

public interface PlaceholderManager {

    void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer, String... placeholders);

    void removePlaceholderReplacer(PlaceholderReplacer placeholderReplacer);

    void removePlaceholderReplacer(String placeholder);

    String handle(PlatformPlayer player, String str);

    String handle(PlatformPlayer player, String str, String prefix);

    String handle(User user, String str);

    String handle(User user, String str, String prefix);

    //String strip(String msg);

    String strip(String msg, String... ignore);
}
