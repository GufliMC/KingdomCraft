package com.gufli.kingdomcraft.bukkit;

import com.gufli.kingdomcraft.bukkit.config.BukkitConfiguration;
import com.gufli.kingdomcraft.common.config.Configuration;
import com.gufli.kingdomcraft.common.messages.MessagesImpl;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MessagesLoader {

    private final static String[] defaultLanguages = new String[] { "en", "nl", "es" };

    public static void load(MessagesImpl messages, ClassLoader classLoader, File directory, String language) {

        // save default files
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

        List<Configuration> configs = new ArrayList<>();

        // english
        URL defaultFallback = classLoader.getResource("languages/en.yml");
        Configuration defaultFallbackConfig = loadResource(defaultFallback);
        configs.add(defaultFallbackConfig);

        // custom language default
        URL fallback = classLoader.getResource("languages/" + language + ".yml");
        if ( fallback != null ) {
            Configuration fallbackConfig = loadResource(fallback);
            if ( fallbackConfig != null ) {
                configs.add(fallbackConfig);
            }
        } else {
            System.out.println("No fallback exists for language '" + language + "'!");
        }

        // load main config file
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(directory, language + ".yml"));
            configs.add(new BukkitConfiguration(config));
        } catch (Exception ex) {
            System.out.println("Unable load custom language file.");
        }

        // load configs into messages
        messages.setMessages(configs.toArray(new Configuration[0]));
    }

    private static Configuration loadResource(URL resource) {
        try (
                InputStream fallback = resource.openStream();
                InputStreamReader isr = new InputStreamReader(fallback, StandardCharsets.UTF_8)
        ){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(isr);
            return new BukkitConfiguration(config);
        } catch (Exception ex) {
            System.out.println("Unable to load fallback language file!");
            ex.printStackTrace();
        }
        return null;
    }

}
