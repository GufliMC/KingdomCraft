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

package com.guflan.kingdomcraft.common.editor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.guflan.kingdomcraft.api.domain.*;
import com.guflan.kingdomcraft.api.editor.EditorAttribute;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class ModelDeserializer {

    private final ObjectMapper mapper = new ObjectMapper();
    private final EditorImpl editor;

    ModelDeserializer(EditorImpl editor) {
        this.editor = editor;
    }

    private void deserialize(AttributeHolder holder, List<EditorAttribute> attributes, JsonNode node) {
        for (EditorAttribute attribute : attributes) {
            if (!node.has(attribute.getName()) ) {
                continue;
            }

            String value = node.get(attribute.getName()).asText();
            if (value == null) {
                continue;
            }

            if (!attribute.validate(value)) {
                editor.kdc.getPlugin().log("Editor tried to set an invalid attribute value.", Level.WARNING);
                continue;
            }

            Attribute a = holder.getAttribute(attribute.getName());
            if (a == null) {
                a = holder.createAttribute(attribute.getName());
            } else if (a.getValue().equals(value)) {
                continue;
            }

            a.setValue(value);
            a.save();
        }
    }

    public void deserialize(Kingdom kingdom, JsonNode node) {
        if (node.has("display") && !kingdom.getDisplay().equals(node.get("display").asText())) {
            kingdom.setDisplay(node.get("display").asText());
        }
        if (node.has("prefix") && !kingdom.getPrefix().equals(node.get("prefix").asText())) {
            kingdom.setPrefix(node.get("prefix").asText());
        }
        if (node.has("suffix") && !kingdom.getSuffix().equals(node.get("suffix").asText())) {
            kingdom.setSuffix(node.get("suffix").asText());
        }
        if (node.has("max-members") && kingdom.getMaxMembers() != node.get("max-members").asInt()) {
            kingdom.setMaxMembers(node.get("max-members").asInt());
        }
        if (node.has("invite-only") && kingdom.isInviteOnly() != node.get("invite-only").asBoolean()) {
            kingdom.setInviteOnly(node.get("invite-only").asBoolean());
        }

        kingdom.save();

        if (node.has("deleted_ranks")) {
            ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {});
            try {
                List<String> ranks = reader.readValue(node.get("deleted_ranks"));
                ranks.stream().map(kingdom::getRank).filter(Objects::nonNull).forEach(Model::delete);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (node.has("ranks")) {
            ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, JsonNode>>() {});
            try {
                Map<String, JsonNode> ranks = reader.readValue(node.get("ranks"));
                for (String rankname : ranks.keySet()) {
                    Rank rank = kingdom.getRank(rankname);
                    if (rank == null) {
                        rank = kingdom.createRank(rankname);
                        rank.save();
                    }
                    deserialize(rank, ranks.get(rankname));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if ( node.has("default_rank") ) {
            Rank rank = kingdom.getRank(node.get("default_rank").asText());
            if ( rank != kingdom.getDefaultRank() ) {
                kingdom.setDefaultRank(rank);
                kingdom.save();
            }
        }

        if (node.has("attributes")) {
            deserialize(kingdom, editor.kingdomAttributes, node.get("attributes"));
        }
    }

    private void deserialize(Rank rank, JsonNode node) {
        if (node.has("display") && !rank.getDisplay().equals(node.get("display").asText())) {
            rank.setDisplay(node.get("display").asText());
        }
        if (node.has("prefix") && !rank.getPrefix().equals(node.get("prefix").asText())) {
            rank.setPrefix(node.get("prefix").asText());
        }
        if (node.has("suffix") && !rank.getSuffix().equals(node.get("suffix").asText())) {
            rank.setSuffix(node.get("suffix").asText());
        }
        if (node.has("max-members") && rank.getMaxMembers() != node.get("max-members").asInt()) {
            rank.setMaxMembers(node.get("max-members").asInt());
        }
        if (node.has("level") && rank.getLevel() != node.get("level").asInt()) {
            rank.setLevel(node.get("level").asInt());
        }

        rank.save();

        if (node.has("attributes")) {
            deserialize(rank, editor.rankAttributes, node.get("attributes"));
        }
    }

}
