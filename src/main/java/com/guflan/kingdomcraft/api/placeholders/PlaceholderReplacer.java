package com.guflan.kingdomcraft.api.placeholders;

import com.guflan.kingdomcraft.api.domain.Player;

public interface PlaceholderReplacer {

    String replace(Player player, String placeholder);

}
