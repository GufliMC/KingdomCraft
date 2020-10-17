package com.guflan.kingdomcraft.api;

public class KingdomCraftProvider {

    private static KingdomCraft instance;

    private KingdomCraftProvider() {}

    public static KingdomCraft get() {
        if (instance == null) {
            throw new IllegalStateException("The KingdomCraft API is not loaded.");
        }
        return instance;
    }

    public static void register(KingdomCraft instance) {
        if ( KingdomCraftProvider.instance != null ) {
            throw new UnsupportedOperationException("Cannot redefine singleton KingdomCraft");
        }
        KingdomCraftProvider.instance = instance;
    }


}
