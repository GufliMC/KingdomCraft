/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.common.commands;

import com.guflan.kingdomcraft.api.entity.PlatformPlayer;
import com.guflan.kingdomcraft.api.entity.PlatformSender;
import com.guflan.kingdomcraft.common.KingdomCraftImpl;
import com.guflan.kingdomcraft.common.command.CommandBase;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends CommandBase {

    public HelpCommand(KingdomCraftImpl kdc) {
        super(kdc, "help");
    }

    @Override
    public void execute(PlatformSender sender, String[] args) {
        List<CommandBase> commands = kdc.getCommandManager().getCommands().stream()
                .filter(cmd -> cmd.getExplanationMessage() != null).collect(Collectors.toList());

        if ( sender instanceof PlatformPlayer ) {
            commands = commands.stream().filter(cmd -> hasAnyPermission(sender, cmd)).collect(Collectors.toList());
        }

        int pagesize = 6;
        int totalpages = (int) Math.ceil(commands.size() * 1d / pagesize);

        int page = 1;
        if ( args.length > 0 && args[0].matches("[0-9]+")) {
            page = Integer.parseInt(args[0]);
        }

        if ( page > totalpages ) {
            kdc.getMessageManager().send(sender, "cmdHelpPageLimit", totalpages + "");
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(kdc.getMessageManager().getMessage("cmdHelpHeader"));
        sb.append("\n");

        for ( int i = (page - 1) * pagesize; i < commands.size(); i++ ) {
            if ( pagesize == 0 ) {
                continue;
            }

            CommandBase cmd = commands.get(i);

            sb.append(kdc.getMessageManager().getMessage("cmdHelpFormat",
                    "/k " + cmd.getCommands().get(0),
                    cmd.getArgumentsHint() == null ? "" : " " + cmd.getArgumentsHint(),
                    cmd.getExplanationMessage()));
            sb.append("\n");

            pagesize--;
        }

        sb.append(kdc.getMessageManager().getMessage("cmdHelpFooter", page + "", totalpages + ""));
        sender.sendMessage(sb.toString());
    }

    private boolean hasAnyPermission(PlatformSender sender, CommandBase command) {
        return Arrays.stream(command.getPermissions()).anyMatch(sender::hasPermission);
    }
}
