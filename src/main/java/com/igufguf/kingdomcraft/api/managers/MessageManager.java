package com.igufguf.kingdomcraft.api.managers;

import com.igufguf.kingdomcraft.api.commands.CommandSender;
import com.igufguf.kingdomcraft.api.domain.Player;

public interface MessageManager {

	public String getMessage(String name);

	public String getMessage(String name, String... placeholders);

	public void send(Player player, String name, String... placeholders);

	public void send(CommandSender sender, String name, String... placeholders);

}
