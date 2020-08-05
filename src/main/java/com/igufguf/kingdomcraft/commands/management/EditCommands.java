package com.igufguf.kingdomcraft.commands.management;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.domain.Kingdom;
import com.igufguf.kingdomcraft.domain.Player;
import com.igufguf.kingdomcraft.models.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
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
public class EditCommands extends CommandBase {

	public EditCommands(KingdomCraft kingdomCraft) {
		super(kingdomCraft, "edit");

		// DISPLAY
		addChild(new Editor(this, "display", new EditorCallback() {
			@Override
			public void onEdit(CommandSender sender, Kingdom kingdom, String value) {
				kingdom.setDisplay(value);
				kingdom.update();
				kingdomCraft.messageHandler.send(sender, "cmdEditSuccess", "display", ChatColor.translateAlternateColorCodes('&', value), kingdom.getName());
			}

			@Override
			public List<String> onAutoComplete() {
				return null;
			}
		}));

		// PREFIX
		addChild(new Editor(this, "prefix", new EditorCallback() {
			@Override
			public void onEdit(CommandSender sender, Kingdom kingdom, String value) {
				kingdom.setPrefix(value);
				kingdom.update();
				kingdomCraft.messageHandler.send(sender, "cmdEditSuccess", "prefix", ChatColor.translateAlternateColorCodes('&', value), kingdom.getName());
			}

			@Override
			public List<String> onAutoComplete() {
				return null;
			}
		}));

		// SUFFIX
		addChild(new Editor(this, "suffix", new EditorCallback() {
			@Override
			public void onEdit(CommandSender sender, Kingdom kingdom, String value) {
				kingdom.setSuffix(value);
				kingdom.update();
				kingdomCraft.messageHandler.send(sender, "cmdEditSuccess", "suffix", ChatColor.translateAlternateColorCodes('&', value), kingdom.getName());
			}

			@Override
			public List<String> onAutoComplete() {
				return null;
			}
		}));

		// INVITE ONLY
		addChild(new Editor(this, "inviteonly", new EditorCallback() {
			@Override
			public void onEdit(CommandSender sender, Kingdom kingdom, String value) {
				if ( !value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false") ) {
					return;
				}

				kingdom.setInviteOnly(Boolean.valueOf(value));
				kingdom.update();
				kingdomCraft.messageHandler.send(sender, "cmdEditSuccess", "friendlyfire", kingdom.isInviteOnly() + "", kingdom.getName());
			}

			@Override
			public List<String> onAutoComplete() {
				return Arrays.asList("true", "false");
			}
		}));
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		sendInvalidUsage(sender);
	}

	private class Editor extends CommandBase {

		private final String option;
		private final EditorCallback callback;

		public Editor(CommandBase parent, String option, EditorCallback callback) {
			super(parent, option);
			this.option = option;
			this.callback = callback;
		}

		@Override
		public List<String> autocomplete(org.bukkit.entity.Player sender, String[] args) {
			if ( sender.hasPermission("kingdom." + option + ".other") ) {
				if ( args.length <= 1 ) {
					return kingdomCraft.kingdomHandler.getKingdoms().stream().map(Kingdom::getName).collect(Collectors.toList());
				} else {
					return callback.onAutoComplete();
				}
			} else if ( sender.hasPermission("kingdom." + option) ) {
				return callback.onAutoComplete();
			}
			return null;
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if ( args.length == 1 ) {
				if ( !sender.hasPermission("kingdom." + option) ) {
					kingdomCraft.messageHandler.send(sender, "noPermission");
					return;
				}

				if ( !(sender instanceof org.bukkit.entity.Player) ) {
					kingdomCraft.messageHandler.send(sender, "cmdPlayerOnly");
					return;
				}

				Player player = kingdomCraft.playerHandler.getPlayer((org.bukkit.entity.Player) sender);
				Kingdom target = player.getKingdom();
				if ( target == null ) {
					kingdomCraft.messageHandler.send(sender, "cmdDefaultSenderNoKingdom");
					return;
				}

				this.callback.onEdit(sender, target, args[0]);
				return;
			}

			if ( args.length == 2 ) {
				if ( !sender.hasPermission("kingdom." + option + ".other") ) {
					kingdomCraft.messageHandler.send(sender, "noPermission");
					return;
				}

				Kingdom target = kingdomCraft.kingdomHandler.getKingdom(args[0]);
				if ( target == null ) {
					kingdomCraft.messageHandler.send(sender, "cmdDefaultKingdomNotExist", args[0]);
					return;
				}

				this.callback.onEdit(sender, target, args[1]);
				return;
			}

			sendInvalidUsage(sender);
		}
	}

	private interface EditorCallback {
		void onEdit(CommandSender sender, Kingdom kingdom, String value);
		List<String> onAutoComplete();
	}

}
