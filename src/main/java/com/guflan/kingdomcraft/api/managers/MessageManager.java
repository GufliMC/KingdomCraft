package com.guflan.kingdomcraft.api.managers;

import com.guflan.kingdomcraft.api.domain.Player;
import com.guflan.kingdomcraft.api.entity.CommandSender;

public interface MessageManager {

	public String getMessage(String name);

	public String getMessage(String name, String... placeholders);

	public void send(Player player, String name, String... placeholders);

	public void send(CommandSender sender, String name, String... placeholders);

}
