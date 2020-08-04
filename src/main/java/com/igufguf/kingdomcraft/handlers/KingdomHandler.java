package com.igufguf.kingdomcraft.handlers;

import com.igufguf.kingdomcraft.KingdomCraft;
import com.igufguf.kingdomcraft.domain.Kingdom;
import com.igufguf.kingdomcraft.domain.KingdomRelation;
import com.igufguf.kingdomcraft.domain.Player;
import com.igufguf.kingdomcraft.domain.query.QKingdomRelation;
import com.igufguf.kingdomcraft.models.Relation;
import io.ebean.Database;

import java.util.List;
import java.util.stream.Collectors;

public class KingdomHandler {

    private final KingdomCraft kingdomCraft;
    private final Database database;

    private final List<Kingdom> kingdoms;
    private final List<KingdomRelation> relations;

    public KingdomHandler(KingdomCraft kingdomCraft, Database database) {
        this.kingdomCraft = kingdomCraft;
        this.database = database;

        // load kingdoms
        this.kingdoms = database.find(Kingdom.class).findList();

        // load relations
        this.relations = database.find(KingdomRelation.class).findList();
    }

    public List<Kingdom> getKingdoms() {
        return kingdoms;
    }

    public Kingdom getKingdom(String name) {
        return kingdoms.stream().filter(k -> k.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Kingdom create(String name) {
        Kingdom kingdom = new Kingdom(name);
        database.save(kingdom);

        return kingdom;
    }

    public boolean delete(Kingdom kingdom) {
        return database.delete(kingdom);
    }

    public void update(Kingdom kingdom) {
        database.update(kingdom);
    }

    public List<Player> getOnlineMembers(Kingdom kingdom) {
        return kingdomCraft.playerHandler.getOnlinePlayers().stream().filter(p -> p.getKingdom() == kingdom).collect(Collectors.toList());
    }

    public Relation getRelation(Kingdom kingdom, Kingdom target) {
        return relations.stream().filter(r -> r.getKingdom() == kingdom && r.getTarget() == target).map(KingdomRelation::getRelation).findFirst().orElse(Relation.NEUTRAL);
    }

    public void changeRelation(Kingdom kingdom, Kingdom target, Relation relation) {

        KingdomRelation existingRelation = new QKingdomRelation(database)
                .kingdom.eq(kingdom).and().target.eq(target).endAnd().or()
                .kingdom.eq(target).and().target.eq(kingdom).endAnd().findOne();

        if (existingRelation != null) {
            if (existingRelation.getRelation() == relation) {
                return;
            }
            database.delete(existingRelation);
        }

        KingdomRelation krel = new KingdomRelation(kingdom, target, relation);
        database.save(krel);
    }

    public List<KingdomRelation> getRelations(Kingdom kingdom) {
        return relations.stream().filter(r -> r.getKingdom() == kingdom).collect(Collectors.toList());
    }

}
