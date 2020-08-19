package com.igufguf.kingdomcraft.common.placeholders;

import com.igufguf.kingdomcraft.api.domain.Player;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderManager;
import com.igufguf.kingdomcraft.api.placeholders.PlaceholderReplacer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultPlaceholderManager implements PlaceholderManager {

    private final Map<String, PlaceholderReplacer> placeholderReplacers = new HashMap<>();

    @Override
    public void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer, String... placeholders) {
        for ( String placeholder : placeholders ) {
            placeholderReplacers.put(placeholder.toLowerCase(), placeholderReplacer);
        }
    }

    @Override
    public void removePlaceholderReplacer(PlaceholderReplacer placeholderReplacer) {
        placeholderReplacers.entrySet().removeIf(pr -> pr == placeholderReplacer);
    }

    @Override
    public void removePlaceholderReplacer(String placeholder) {
        placeholderReplacers.remove(placeholder);
    }

    @Override
    public String handle(Player player, String str) {
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("(\\{[^}]+\\})");
        Matcher m = p.matcher(str);

        while ( m.find() ) {
            String placeholder = m.group(1).toLowerCase();
            placeholder = placeholder.substring(1, placeholder.length()-1); // remove brackets { and }

            PlaceholderReplacer replacer = placeholderReplacers.get(placeholder);
            if ( replacer == null ) {
                continue;
            }

            String replacement = replacer.replace(player, placeholder);
            if ( replacement == null ) {
                replacement = "";
            }
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
