package com.gufli.kingdomcraft.bukkit;

import com.gufli.kingdomcraft.bukkit.config.BukkitConfiguration;
import com.gufli.kingdomcraft.common.messages.MessagesImpl;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MessagesLoader {

    private final static String[] defaultLanguages = new String[] { "en", "nl", "es" };

    public static void load(MessagesImpl messages, ClassLoader classLoader, File directory, String language) {
        for ( String lang : defaultLanguages ) {
            File outFile = new File(directory, lang + ".yml");
            if ( !outFile.exists() ) {
                URL inurl = classLoader.getResource("languages/" + lang + ".yml");
                if ( inurl == null ) continue;
                try (
                        InputStream in = inurl.openStream();
                        OutputStream out = new FileOutputStream(outFile)
                ){

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(directory, language + ".yml"));
            messages.setMessages(new BukkitConfiguration(config));
        } catch (Exception ex) {
            System.out.println("Unable load custom language file.");
        }

        URL resource;
        resource = classLoader.getResource("languages/" + language + ".yml");
        if ( resource == null ) {
            resource = classLoader.getResource("languages/en.yml");
        }

        if ( resource == null ) {
            System.out.println("Cannot find fallback language file!");
            return;
        }

        try (
                InputStream fallback = resource.openStream();
                InputStreamReader isr = new InputStreamReader(fallback, StandardCharsets.UTF_8)
        ){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(isr);
            messages.setFallback(new BukkitConfiguration(config));
        } catch (Exception ex) {
            System.out.println("Unable to load fallback language file!");
            ex.printStackTrace();
        }
    }

}
