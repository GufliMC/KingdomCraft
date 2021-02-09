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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gufli.kingdomcraft.api.domain.Kingdom;
import com.gufli.kingdomcraft.api.domain.Model;
import com.gufli.kingdomcraft.api.editor.Editor;
import com.gufli.kingdomcraft.api.editor.EditorAttribute;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EditorImpl implements Editor {

    private final static String editorUrl = "https://webeditor.kingdomcraft.be/";

    private final static String uploadUrl = "https://bytebin.kingdomcraft.be/";
    private final static String fetchUrl = "https://bytebin.kingdomcraft.be/";

    final KingdomCraftImpl kdc;

    final List<EditorAttribute> kingdomAttributes = new CopyOnWriteArrayList<>();
    final List<EditorAttribute> rankAttributes = new CopyOnWriteArrayList<>();

    private final ModelSerializer serializer = new ModelSerializer(this);
    private final ModelDeserializer deserializer = new ModelDeserializer(this);

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

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        Map<String, Map<String, Object>> kingdoms = new HashMap<>();
        kdc.getKingdoms(true).forEach(kingdom -> kingdoms.put(kingdom.getName(), serializer.serialize(kingdom)));
        node.set("kingdoms", mapper.valueToTree(kingdoms));

        Map<String, String> kingdomAttributes = new HashMap<>();
        this.kingdomAttributes.forEach(attribute -> kingdomAttributes.put(attribute.getName(), attribute.getDescription()));
        node.set("kingdomAttributes", mapper.valueToTree(kingdomAttributes));

        Map<String, String> rankAttributes = new HashMap<>();
        this.rankAttributes.forEach(attribute -> rankAttributes.put(attribute.getName(), attribute.getDescription()));
        node.set("rankAttributes", mapper.valueToTree(rankAttributes));
        
        try {
            String result = upload(mapper.writeValueAsString(node));
            JsonNode resultNode = mapper.readTree(result);
            kdc.getMessages().send(sender, "cmdEditorStarted", editorUrl + resultNode.get("key").asText());
        } catch (Exception e) {
            e.printStackTrace();
            kdc.getMessages().send(sender, "cmdEditorErrorUpload");
        }
    }

    public void finish(PlatformSender sender, String resultKey) {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json;
        try {
            String result = fetch(resultKey);
            json = mapper.readTree(result);
        }  catch (JsonProcessingException e) {
            e.printStackTrace();
            kdc.getMessages().send(sender, "cmdEditorErrorFormat");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            kdc.getMessages().send(sender, "cmdEditorErrorDownload");
            return;
        }

        try {
            if ( json.has("deleted_kingdoms") ) {
                ObjectReader reader = mapper.readerFor(new TypeReference<List<String>>() {});
                List<String> kingdoms = reader.readValue(json.get("deleted_kingdoms"));
                kingdoms.stream().map(kdc::getKingdom).filter(Objects::nonNull).forEach(Model::delete);
            }

            if ( !json.has("kingdoms") ) {
                kdc.getMessages().send(sender, "cmdEditorErrorFormat");
                return;
            }

            ObjectReader reader = mapper.readerFor(new TypeReference<Map<String, JsonNode>>() {});
            Map<String, JsonNode> kingdoms = reader.readValue(json.get("kingdoms"));

            for ( String key : kingdoms.keySet() ) {
                key = key.replace(" ", "");
                Kingdom kingdom = kdc.getKingdom(key);
                if ( kingdom == null ) {
                    kingdom = kdc.createKingdom(key);
                }
                deserializer.deserialize(kingdom, kingdoms.get(key));
            }

            kdc.getPlugin().reload();

            kdc.getMessages().send(sender, "cmdEditorSaved");
        } catch (Exception ex) {
            ex.printStackTrace();
            kdc.getMessages().send(sender, "cmdEditorErrorSave");
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
