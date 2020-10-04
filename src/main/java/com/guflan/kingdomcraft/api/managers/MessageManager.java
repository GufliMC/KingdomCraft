package com.guflan.kingdomcraft.api.managers;

import com.guflan.kingdomcraft.api.entity.CommandSender;
import com.guflan.kingdomcraft.api.entity.Player;

public interface MessageManager {

	String getMessage(String name);

	String getMessage(String name, String... placeholders);

	void send(Player player, String name, String... placeholders);

	void send(CommandSender sender, String name, String... placeholders);

	String colorify(String msg);

	String decolorify(String msg);

}
