package com.igufguf.kingdomcraft.api.managers;

import com.igufguf.kingdomcraft.api.models.Player;

public interface MessageManager {

	public String getMessage(String name);

	public String getMessage(String name, String... placeholders);

	public void send(Player player, String name, String... placeholders);

}
