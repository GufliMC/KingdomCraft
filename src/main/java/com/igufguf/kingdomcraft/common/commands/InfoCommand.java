package com.igufguf.kingdomcraft.common.commands;

import com.igufguf.kingdomcraft.bukkit.KingdomCraft;
import com.igufguf.kingdomcraft.common.domain.DKingdom;
import com.igufguf.kingdomcraft.common.domain.DKingdomRelation;
import com.igufguf.kingdomcraft.common.domain.DPlayer;
import com.igufguf.kingdomcraft.common.models.Relation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyrighted 2020 iGufGuf
 *
 * This file is part of KingdomCraft.
 *
 * Kingdomcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
public class InfoCommand extends CommandBase {

	private List<InfoCallback> callbacks;

	public InfoCommand(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "info");

		addCallback(new InfoCallback() {
			@Override
			public void onKingdomInfo(InfoBuilder builder, DKingdom kingdom) {
				List<DKingdomRelation> relations = kingdomCraft.kingdomHandler.getRelations(kingdom);

				builder.add("friendlies", ChatColor.GRAY + "Friendly: " + relations.stream()
								.filter(r -> r.getRelation() == Relation.FRIENDLY)
								.map(r -> r.getKingdom() == kingdom ? r.getTarget().getName() : r.getKingdom().getName())
								.collect(Collectors.joining(ChatColor.GRAY + ", ")));

				builder.add("enemies", ChatColor.GRAY + "Enemy: " + relations.stream()
						.filter(r -> r.getRelation() == Relation.ENEMY)
						.map(r -> r.getKingdom() == kingdom ? r.getTarget().getName() : r.getKingdom().getName())
						.collect(Collectors.joining(ChatColor.GRAY + ", ")));

				builder.add("members", ChatColor.GRAY + "Members: " + kingdom.getMembers().stream()
						.map(p -> ChatColor.WHITE + p.getName()).sorted()
						.collect(Collectors.joining(ChatColor.GRAY + ", ")));
			}

			@Override
			public void onPlayerInfo(InfoBuilder builder, DPlayer player) {
				if ( player.getKingdom() != null ) {
					builder.add("kingdom", ChatColor.WHITE + player.getName()
							+ ChatColor.GRAY + " is in kingdom " + player.getKingdom().getDisplay()
							+ ChatColor.GRAY + " with rank " + player.getRank().getDisplay() + ChatColor.GRAY + ".");
				}
			}
		});
	}

	public void addCallback(InfoCallback callback) {
		callbacks.add(callback);
	}

	private InfoBuilder build(DKingdom kingdom) {
		InfoBuilder builder = new InfoBuilder();
		for ( InfoCallback cb : callbacks ) {
			cb.onKingdomInfo(builder, kingdom);
		}
		return builder;
	}

	private InfoBuilder build(DPlayer player) {
		InfoBuilder builder = new InfoBuilder();
		for ( InfoCallback cb : callbacks ) {
			cb.onPlayerInfo(builder, player);
		}
		return builder;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {

		if ( args.length == 0 ) {
			DPlayer player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);
			if ( player.getKingdom() != null ) {
				DKingdom kingdom = player.getKingdom();
				sender.sendMessage(kingdomCraft.messageHandler.getMessage("cmdInfoKingdom", kingdom.getDisplay()) + "\n" + build(kingdom));
				return;
			}

			sender.sendMessage(kingdomCraft.messageHandler.getMessage("cmdInfoPlayer", player.getName()) + "\n" + build(player));
			return;
		}

		DPlayer player = kingdomCraft.playerHandler.getPlayer(Bukkit.getPlayer(args[0]));
		if ( player != null ) {
			sender.sendMessage(kingdomCraft.messageHandler.getMessage("cmdInfoPlayer", player.getName()) + "\n" + build(player));
			return;
		}

		DKingdom kingdom = kingdomCraft.kingdomHandler.getKingdom(args[0]);
		if ( kingdom != null ) {
			sender.sendMessage(kingdomCraft.messageHandler.getMessage("cmdInfoKingdom", kingdom.getName()) + "\n" + build(kingdom));
			return;
		}

		kingdomCraft.messageHandler.send(sender, "cmdDefaultTargetNoKingdom");
	}

	public class InfoBuilder {

		private Map<String, String> lines = new HashMap<>();

		public void add(String key, String line) {
			lines.put(key, line);
		}

		public String build() {
			return ChatColor.translateAlternateColorCodes('&', String.join("\n", lines.values()));
		}

	}

	public interface InfoCallback {
		void onKingdomInfo(InfoBuilder builder, DKingdom kingdom);
		void onPlayerInfo(InfoBuilder builder, DPlayer player);
	}

}
