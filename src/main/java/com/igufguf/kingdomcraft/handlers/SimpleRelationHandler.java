package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.api.handlers.KingdomRelationHandler;
import com.igufguf.kingdomcraft.api.models.database.StorageManager;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRelation;

import java.io.File;
import java.util.HashMap;

/**
 * Copyrighted 2018 iGufGuf
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
public class SimpleRelationHandler extends StorageManager implements KingdomRelationHandler {

    private final KingdomCraft plugin;

    public SimpleRelationHandler(KingdomCraft plugin) {
        super(new File(plugin.getDataFolder() + "/data", "relations.data"));
        this.plugin = plugin;
    }

    @Override
    public KingdomRelation getRelation(Kingdom kingdom, Kingdom target) {
        String relname = null;
        if ( getStorageData().contains(kingdom.getName() + "-" + target.getName()) ) {
            relname = getStorageData().getString(kingdom.getName() + "-" + target.getName());
        }
        if ( relname == null ) return KingdomRelation.NEUTRAL;

        for ( KingdomRelation rel : KingdomRelation.values() ) {
            if ( rel.name().equalsIgnoreCase(relname) ) return rel;
        }
        return KingdomRelation.NEUTRAL;
    }

    @Override
    public void setRelation(Kingdom kingdom, Kingdom target, KingdomRelation relation) {
        getStorageData().set(kingdom.getName() + "-" + target.getName(), relation != null ? relation.name() : null);
        save();
    }

    @Override
    public HashMap<Kingdom, KingdomRelation> getRelations(Kingdom kingdom) {
        HashMap<Kingdom, KingdomRelation> relations = new HashMap<>();
        for ( Kingdom ko : plugin.getApi().getKingdomHandler().getKingdoms() ) {
            if ( ko != kingdom ) relations.put(ko, getRelation(kingdom, ko));
        }
        return relations;
    }
}
