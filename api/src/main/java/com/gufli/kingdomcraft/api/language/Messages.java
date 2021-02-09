package com.gufli.kingdomcraft.api.language;

import com.gufli.kingdomcraft.api.entity.PlatformSender;

public interface Messages {

    String getPrefix();

    String getMessage(String name);

    String getMessage(String name, boolean colorifyPlaceholders, String... placeholders);

    String getMessage(String name, String... placeholders);

    void send(PlatformSender sender, String name, String... placeholders);

    void send(PlatformSender sender, String name, boolean colorifyPlaceholders, String... placeholders);

    String colorify(String msg);

    String decolorify(String msg);

}
