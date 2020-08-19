package com.igufguf.kingdomcraft.api.placeholders;

import com.igufguf.kingdomcraft.api.domain.Player;

public interface PlaceholderReplacer {

    String replace(Player player, String placeholder);

}
