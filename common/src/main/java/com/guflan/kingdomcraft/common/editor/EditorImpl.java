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

package com.guflan.kingdomcraft.common.editor;

import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.KingdomAttribute;
import com.guflan.kingdomcraft.api.domain.Rank;
import com.guflan.kingdomcraft.api.domain.RankAttribute;
import com.guflan.kingdomcraft.api.editor.Editor;
import com.guflan.kingdomcraft.api.editor.EditorAttribute;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class EditorImpl implements Editor {

    private final static String editorUrl = "https://bytebin.lucko.me/";

    private final static String uploadUrl = "https://bytebin.lucko.me/post";
    private final static String fetchUrl = "https://bytebin.lucko.me/";

    private final KingdomCraftImpl kdc;

    private final List<EditorAttribute> kingdomAttributes = new CopyOnWriteArrayList<>();
    private final List<EditorAttribute> rankAttributes = new CopyOnWriteArrayList<>();

    public EditorImpl(KingdomCraftImpl kdc) {
        this.kdc = kdc;
    }

    @Override
    public void addKingdomAttribute(EditorAttribute attribute) {
        if (kingdomAttributes.contains(attribute)) {
            return;
        }
        kingdomAttributes.add(attribute);
    }

    @Override
    public void removeKingdomAttribute(EditorAttribute attribute) {
        kingdomAttributes.remove(attribute);
    }

    @Override
    public void addRankAttribute(EditorAttribute attribute) {
        if (rankAttributes.contains(attribute)) {
            return;
        }
        rankAttributes.add(attribute);
    }

    @Override
    public void removeRankAttribute(EditorAttribute attribute) {
        rankAttributes.remove(attribute);
    }

    public void startSession(PlatformSender sender) {
        JSONArray kingdomAttributes = new JSONArray();
        for (EditorAttribute attribute : this.kingdomAttributes) {
            kingdomAttributes.add(serialize(attribute));
        }

        JSONArray rankAttributes = new JSONArray();
        for (EditorAttribute attribute : this.kingdomAttributes) {
            rankAttributes.add(serialize(attribute));
        }

        JSONObject kingdoms = new JSONObject();
        for (Kingdom kingdom : kdc.getKingdoms()) {
            kingdoms.put(kingdom.getName(), serialize(kingdom));
        }

        JSONObject json = new JSONObject();
        json.put("kingdomAttributes", kingdomAttributes);
        json.put("rankAttributes", rankAttributes);
        json.put("kingdoms", kingdoms);

        System.out.println(json);

        try {
            String result = upload(json.toJSONString());
            JSONObject resultJson = (JSONObject) new JSONParser().parse(result);
            kdc.getMessageManager().send(sender, "cmdEditorStarted", editorUrl + resultJson.get("key"));
        } catch (IOException e) {
            e.printStackTrace();
            kdc.getMessageManager().send(sender, "cmdEditorErrorUpload");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void finish(PlatformSender sender, String resultKey) {
        JSONObject json;
        try {
            String result = fetch(resultKey);
            json = (JSONObject) new JSONParser().parse(result);
        } catch (IOException e) {
            e.printStackTrace();
            kdc.getMessageManager().send(sender, "cmdEditorErrorDownload");
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            kdc.getMessageManager().send(sender, "cmdEditorErrorFormat");
            return;
        }

        try {
            Map<String, JSONObject> kingdoms = (JSONObject) json.get("kingdoms");
            for (Kingdom kingdom : kdc.getKingdoms()) {
                if (!kingdoms.containsKey(kingdom.getName())) {
                    kingdom.delete();
                } else {
                    JSONObject kingdomJson = kingdoms.get(kingdom.getName());
                    deserialize(kingdom, kingdomJson);
                }
            }
            for (String name : kingdoms.keySet()) {
                Kingdom kingdom = kdc.getKingdom(name);
                if (kingdom != null) {
                    continue;
                }

                JSONObject kingdomJson = kingdoms.get(name);
                kingdom = kdc.createKingdom(name);
                deserialize(kingdom, kingdomJson);
            }

            kdc.getMessageManager().send(sender, "cmdEditorSaved");
        } catch (Exception ex) {
            ex.printStackTrace();
            kdc.getMessageManager().send(sender, "cmdEditorErrorSave");
        }
    }

    private JSONObject serialize(EditorAttribute attribute) {
        JSONObject json = new JSONObject();
        json.put("name", attribute.getName());
        json.put("description", attribute.getDescription());
        return json;
    }

    private JSONObject serialize(Kingdom kingdom) {
        JSONObject json = new JSONObject();
        json.put("display", kingdom.getDisplay());
        json.put("prefix", kingdom.getPrefix());
        json.put("suffix", kingdom.getSuffix());
        json.put("defaultrank", kingdom.getDefaultRank().getName());
        json.put("max-members", kingdom.getMaxMembers());
        json.put("invite-only", kingdom.isInviteOnly());

        JSONObject attributes = new JSONObject();
        for (EditorAttribute attribute : this.kingdomAttributes) {
            KingdomAttribute kingdomAttribute = kingdom.getAttribute(attribute.getName());
            if (kingdomAttribute != null) {
                attributes.put(attribute.getName(), kingdomAttribute.getValue());
            }
        }
        json.put("attributes", attributes);

        JSONObject ranks = new JSONObject();
        for (Rank rank : kingdom.getRanks()) {
            ranks.put(rank.getName(), serialize(rank));
        }
        json.put("ranks", ranks);

        return json;
    }

    private JSONObject serialize(Rank rank) {
        JSONObject json = new JSONObject();
        json.put("display", rank.getDisplay());
        json.put("prefix", rank.getPrefix());
        json.put("suffix", rank.getSuffix());
        json.put("max-members", rank.getMaxMembers());
        json.put("level", rank.getLevel());

        JSONObject attributes = new JSONObject();
        for (EditorAttribute attribute : this.rankAttributes) {
            RankAttribute rankAttribute = rank.getAttribute(attribute.getName());
            if (rankAttribute != null) {
                attributes.put(attribute.getName(), rankAttribute.getValue());
            }
        }
        json.put("attributes", attributes);

        return json;
    }

    private void deserialize(Kingdom kingdom, JSONObject json) {
        boolean updated = false;
        if (json.containsKey("name")
                && !kingdom.getName().equals(json.get("name"))) {
            try {
                kingdom.renameTo((String) json.get("name"));
                updated = true;
            } catch (Exception ignore) {
                kdc.getPlugin().log("Editor tried to rename to an existing kingdom.", Level.WARNING);
            }
        }
        if (json.containsKey("display") && !kingdom.getDisplay().equals(json.get("display"))) {
            kingdom.setDisplay((String) json.get("display"));
            updated = true;
        }
        if (json.containsKey("prefix") && !kingdom.getPrefix().equals(json.get("prefix"))) {
            kingdom.setPrefix((String) json.get("prefix"));
            updated = true;
        }
        if (json.containsKey("suffix") && !kingdom.getSuffix().equals(json.get("suffix"))) {
            kingdom.setSuffix((String) json.get("suffix"));
            updated = true;
        }
        if (json.containsKey("max-members") && kingdom.getMaxMembers() != (int) (long) json.get("max-members")) {
            kingdom.setMaxMembers((int) (long) json.get("max-members"));
            updated = true;
        }
        if (json.containsKey("invite-only") && kingdom.isInviteOnly() != (boolean) json.get("invite-only")) {
            kingdom.setInviteOnly((boolean) json.get("invite-only"));
            updated = true;
        }

        if (json.containsKey("attributes")) {
            Map<String, Object> attributesJson = (JSONObject) json.get("attributes");
            for (EditorAttribute attribute : this.kingdomAttributes) {
                if (attributesJson.get(attribute.getName()) == null) {
                    continue;
                }
                String value = attributesJson.get(attribute.getName()).toString();
                if (!attribute.validate(value)) {
                    kdc.getPlugin().log("Editor tried to set an invalid kingdom attribute value.", Level.WARNING);
                    continue;
                }

                KingdomAttribute kingdomAttribute = kingdom.getAttribute(attribute.getName());
                if (kingdomAttribute == null) {
                    kingdomAttribute = kingdom.createAttribute(attribute.getName());
                } else if (kingdomAttribute.getValue().equals(value)) {
                    continue;
                }
                kingdomAttribute.setValue(value);
                kingdomAttribute.save();
            }
        }

        // change default rank before deleting
//        if ( deserializeDefaultRank(kingdom, json) ) {
//            updated = true;
//        }

        if (json.containsKey("ranks")) {
            Map<String, JSONObject> ranks = (JSONObject) json.get("ranks");
            for (Rank rank : kingdom.getRanks()) {
                if (!json.containsKey(rank.getName())) {
                    if (kingdom.getDefaultRank() == rank) {
                        kdc.getPlugin().log("Editor tried to delete the default rank.", Level.WARNING);
                        continue;
                    }
                    rank.delete();
                } else {
                    JSONObject rankJson = (JSONObject) json.get(rank.getName());
                    deserialize(rank, rankJson);
                }
            }
            for (String name : ranks.keySet()) {
                Rank rank = kingdom.getRank(name);
                if (rank != null) {
                    continue;
                }

                JSONObject rankJson = ranks.get(name);
                rank = kingdom.createRank(name);
                deserialize(rank, rankJson);
            }
        }

//        if ( deserializeDefaultRank(kingdom, json) ) {
//            updated = true;
//        }

        if (updated) {
            kingdom.save();
        }
    }

    private boolean deserializeDefaultRank(Kingdom kingdom, JSONObject json) {
        if (json.containsKey("defaultrank") && (kingdom.getDefaultRank() == null
                || !kingdom.getDefaultRank().getName().equals(json.get("defaultrank")))) {
            Rank rank = kingdom.getRank((String) json.get("defaultrank"));
            if (rank != null) {
                kingdom.setDefaultRank(rank);
                return true;
            }
        }
        return false;
    }

    private void deserialize(Rank rank, JSONObject json) {
        boolean updated = false;

        if (json.containsKey("name")
                && !rank.getName().equals(json.get("name"))) {
            try {
                rank.renameTo((String) json.get("name"));
                updated = true;
            } catch (Exception ex) {
                kdc.getPlugin().log("Editor tried to rename to an existing rank.", Level.WARNING);
            }
        }
        if (json.containsKey("display") && !rank.getDisplay().equals(json.get("display"))) {
            rank.setDisplay((String) json.get("display"));
            updated = true;
        }
        if (json.containsKey("prefix") && !rank.getPrefix().equals(json.get("prefix"))) {
            rank.setPrefix((String) json.get("prefix"));
            updated = true;
        }
        if (json.containsKey("suffix") && !rank.getSuffix().equals(json.get("suffix"))) {
            rank.setSuffix((String) json.get("suffix"));
            updated = true;
        }
        if (json.containsKey("max-members") && rank.getMaxMembers() != (int) (long) json.get("max-members")) {
            rank.setMaxMembers((int) (long) json.get("max-members"));
            updated = true;
        }
        if (json.containsKey("level") && rank.getLevel() != (int) (long) json.get("level")) {
            rank.setLevel((int) (long) json.get("level"));
            updated = true;
        }

        if (json.containsKey("attributes")) {
            Map<String, Object> attributesJson = (JSONObject) json.get("attributes");
            for (EditorAttribute attribute : this.rankAttributes) {
                if (attributesJson.get(attribute.getName()) == null) {
                    continue;
                }
                String value = attributesJson.get(attribute.getName()).toString();
                if (!attribute.validate(value)) {
                    kdc.getPlugin().log("Editor tried to set an invalid rank attribute value.", Level.WARNING);
                    continue;
                }

                RankAttribute rankAttribute = rank.getAttribute(attribute.getName());
                if (rankAttribute == null) {
                    rankAttribute = rank.createAttribute(attribute.getName());
                } else if (rankAttribute.getValue().equals(value)) {
                    continue;
                }
                rankAttribute.setValue(value);
                rankAttribute.save();
            }
        }

        if (updated) {
            rank.save();
        }
    }

    private String upload(String contents) throws IOException {
        HttpPost post = new HttpPost(uploadUrl);
        post.setEntity(EntityBuilder.create()
                .setText(contents)
                .setContentType(ContentType.APPLICATION_JSON)
                .gzipCompress()
                .build());

        try (
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(post)
        ) {
            return parseResults(response);
        }
    }

    private String fetch(String key) throws IOException {
        HttpGet get = new HttpGet(fetchUrl + key);

        try (
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(get)
        ) {
            return parseResults(response);
        }
    }

    private String parseResults(CloseableHttpResponse response) throws IOException {
        try (
                InputStream responseBody = response.getEntity().getContent();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
        ) {

            int status = response.getStatusLine().getStatusCode();

            if (status < 200 || status >= 300) {
                throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
            }

            byte[] buffer = new byte[1024];
            int length;
            while ((length = responseBody.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString(StandardCharsets.UTF_8.name());
        }
    }

}
