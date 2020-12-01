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

package com.gufli.kingdomcraft.common.editor;

import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.KingdomAttribute;
import com.gufli.kingdomcraft.api.domain.Rank;
import com.gufli.kingdomcraft.api.domain.RankAttribute;
import com.gufli.kingdomcraft.api.editor.EditorAttribute;

import java.util.*;

public class ModelSerializer {

    private final EditorImpl editor;

    ModelSerializer(EditorImpl editor) {
        this.editor = editor;
    }

    private Map<String, String> serialize(EditorAttribute attribute) {
        Map<String, String> map = new HashMap<>();
        map.put("name", attribute.getName());
        map.put("description", attribute.getDescription());
        return map;
    }

    public Map<String, Object> serialize(Kingdom kingdom) {
        Map<String, Object> map = new HashMap<>();
        map.put("display", kingdom.getDisplay());
        map.put("prefix", kingdom.getPrefix());
        map.put("suffix", kingdom.getSuffix());
        map.put("defaultrank", kingdom.getDefaultRank() != null ? kingdom.getDefaultRank().getName() : "");
        map.put("max-members", kingdom.getMaxMembers());
        map.put("invite-only", kingdom.isInviteOnly());

        // remove empty values
        new HashSet<>(map.keySet()).stream().filter(key -> map.get(key).equals("")).forEach(map::remove);

        if ( !editor.kingdomAttributes.isEmpty() ) {
            Map<String, String> attributes = new HashMap<>();
            for (EditorAttribute attribute : editor.kingdomAttributes) {
                KingdomAttribute ka = kingdom.getAttribute(attribute.getName());
                if (ka != null) {
                    attributes.put(attribute.getName(), ka.getValue());
                } else {
                    attributes.put(attribute.getName(), "");
                }
            }
            map.put("attributes", attributes);
        }

        Map<String, Map<String, Object>> ranks = new HashMap<>();
        kingdom.getRanks().forEach(rank -> ranks.put(rank.getName(), serialize(rank)));
        if ( !ranks.isEmpty() ) {
            map.put("ranks", ranks);
        }

        return map;
    }


    private Map<String, Object> serialize(Rank rank) {
        Map<String, Object> map = new HashMap<>();
        map.put("display", rank.getDisplay());
        map.put("prefix", rank.getPrefix());
        map.put("suffix", rank.getSuffix());
        map.put("max-members", rank.getMaxMembers());
        map.put("level", rank.getLevel());

        // remove empty values
        new HashSet<>(map.keySet()).stream().filter(key -> map.get(key).equals("")).forEach(map::remove);

        if ( !editor.rankAttributes.isEmpty() ) {
            Map<String, String> attributes = new HashMap<>();
            for (EditorAttribute attribute : editor.rankAttributes) {
                RankAttribute ra = rank.getAttribute(attribute.getName());
                if (ra != null) {
                    attributes.put(attribute.getName(), ra.getValue());
                } else {
                    attributes.put(attribute.getName(), "");
                }
            }
            map.put("attributes", attributes);
        }

        return map;
    }


}
