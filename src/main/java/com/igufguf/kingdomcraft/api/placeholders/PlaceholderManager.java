package com.igufguf.kingdomcraft.api.placeholders;

import com.igufguf.kingdomcraft.api.domain.Player;

public interface PlaceholderManager {

    void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer, String... placeholders);

    void removePlaceholderReplacer(PlaceholderReplacer placeholderReplacer);

    void removePlaceholderReplacer(String placeholder);

    String handle(Player player, String str);

}
