/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gufli.kingdomcraft.common.commands.general;

import com.gufli.kingdomcraft.api.commands.Command;
import com.gufli.kingdomcraft.api.entity.PlatformPlayer;
import com.gufli.kingdomcraft.api.entity.PlatformSender;
import com.gufli.kingdomcraft.common.KingdomCraftImpl;
import com.gufli.kingdomcraft.common.command.CommandBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends CommandBase {

    private final static int pagesize = 6;

    public HelpCommand(KingdomCraftImpl kdc) {
        super(kdc, "help");
    }

    @Override
    public List<String> autocomplete(PlatformPlayer player, String[] args) {
        if ( args.length == 1 ) {
            List<Command> commands = getAvailableCommands(player);
            int totalpages = (int) Math.ceil(commands.size() * 1d / pagesize);
            List<String> result = new ArrayList<>();
            for ( int i = 1; i <= totalpages; i++ ) {
                result.add(i + "");
            }
            return result;
        }
        return null;
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        List<Command> commands = getAvailableCommands(sender);

        int totalpages = (int) Math.ceil(commands.size() * 1d / pagesize);

        int page = 1;
        if ( args.length > 0 && args[0].matches("[0-9]+")) {
            page = Integer.parseInt(args[0]);
        }

        if ( page > totalpages ) {
            kdc.getMessages().send(sender, "cmdHelpPageLimit", totalpages + "");
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(kdc.getMessages().getMessage("cmdHelpHeader"));
        sb.append("\n");

        int startindex = (page - 1) * pagesize;
        for ( int i = startindex; i < Math.min(commands.size(), startindex + pagesize); i++ ) {
            Command cmd = commands.get(i);

            String command = "/k " + cmd.getCommands().get(0) + (cmd.getArgumentsHint() == null ? "" : " " + cmd.getArgumentsHint());
            String explanation = kdc.getMessages().getMessage(cmd.getExplanationMessage());

            sb.append(kdc.getMessages().getMessage("cmdHelpFormat", command, explanation));
            sb.append("\n");
        }

        sb.append(kdc.getMessages().getMessage("cmdHelpFooter", page + "", totalpages + ""));
        sender.sendMessage(sb.toString());
    }

    private List<Command> getAvailableCommands(PlatformSender sender) {
        List<Command> commands = kdc.getCommandManager().getCommands().stream()
                .filter(cmd -> kdc.getMessages().getMessage(cmd.getExplanationMessage()) != null).collect(Collectors.toList());

        if ( sender instanceof PlatformPlayer ) {
            commands = commands.stream().filter(cmd -> hasAnyPermission(sender, cmd)).collect(Collectors.toList());
        }
        return commands;
    }

    private boolean hasAnyPermission(PlatformSender sender, Command command) {
        return Arrays.stream(command.getPermissions()).anyMatch(sender::hasPermission);
    }
}
