package com.guflan.kingdomcraft.api.placeholders;

import com.guflan.kingdomcraft.api.entity.Player;

public interface PlaceholderManager {

    void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer, String... placeholders);

    void removePlaceholderReplacer(PlaceholderReplacer placeholderReplacer);

    void removePlaceholderReplacer(String placeholder);

    String handle(Player player, String str);

}
