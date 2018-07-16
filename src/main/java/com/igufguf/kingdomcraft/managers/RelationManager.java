package com.igufguf.kingdomcraft.managers;

import com.igufguf.kingdomcraft.KingdomCraftApi;
import com.igufguf.kingdomcraft.objects.KingdomObject;
import com.igufguf.kingdomcraft.objects.KingdomRelation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
public class RelationManager {

    private final KingdomCraftApi api;

    private final File file;
    private final FileConfiguration data;

    public RelationManager(KingdomCraftApi api) throws IOException {
        this.api = api;

        file = new File(api.getPlugin().getDataFolder() + "/data", "relations.yml");
        if ( !file.exists() ) {
            file.createNewFile();
        }

        data = YamlConfiguration.loadConfiguration(file);
    }

    public KingdomRelation getRelation(KingdomObject kingdom, KingdomObject target) {
        String relname = null;
        if ( data.contains(kingdom.getName() + "-" + target.getName()) ) {
            relname = data.getString(kingdom.getName() + "-" + target.getName());
        }
        if ( relname == null ) return KingdomRelation.NEUTRAL;

        for ( KingdomRelation rel : KingdomRelation.values() ) {
            if ( rel.name().equalsIgnoreCase(relname) ) return rel;
        }
        return KingdomRelation.NEUTRAL;
    }

    public void setRelation(KingdomObject kingdom, KingdomObject target, KingdomRelation relation) {
        data.set(kingdom.getName() + "-" + target.getName(), relation != null ? relation.name() : null);

        try {
            data.save(new File(api.getPlugin().getDataFolder(), "relations.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<KingdomObject, KingdomRelation> getRelations(KingdomObject kingdom) {
        HashMap<KingdomObject, KingdomRelation> relations = new HashMap<>();
        for ( KingdomObject ko : api.getKingdomManager().getKingdoms() ) {
            if ( ko != kingdom ) relations.put(ko, getRelation(kingdom, ko));
        }
        return relations;
    }

}
