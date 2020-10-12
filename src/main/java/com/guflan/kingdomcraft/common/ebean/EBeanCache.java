package com.guflan.kingdomcraft.common.ebean;

import com.guflan.kingdomcraft.api.domain.DomainCache;
import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.User;
import com.guflan.kingdomcraft.common.ebean.beans.BKingdom;
import com.guflan.kingdomcraft.common.ebean.beans.BRank;
import com.guflan.kingdomcraft.common.ebean.beans.BRelation;
import com.guflan.kingdomcraft.common.ebean.beans.BUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EBeanCache implements DomainCache {

    private final List<BKingdom> kingdoms = new ArrayList<>();
    private final List<BRelation> relations = new ArrayList<>();

    private final List<BUser> users = new ArrayList<>();

    @Override
    public List<Kingdom> getKingdoms() {
        return new ArrayList<>(kingdoms);
    }

    @Override
    public Kingdom getKingdom(String name) {
        return kingdoms.stream().filter(k -> k.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void watch(Kingdom kingdom) {
        if ( !kingdoms.contains(kingdom) ) {
            kingdoms.add((BKingdom) kingdom);
        }
    }

    @Override
    public void unwatch(Kingdom kingdom) {
        kingdoms.remove(kingdom);

        for ( BUser user : users ) {
            user.kingdom = null;
            user.rank = null;
        }
    }

    // relations

    @Override
    public List<Relation> getRelations() {
        return new ArrayList<>(relations);
    }

    @Override
    public List<Relation> getRelations(Kingdom kingdom) {
        return relations.stream().filter(r -> r.kingdom.equals(kingdom) || r.otherKingdom.equals(kingdom)).collect(Collectors.toList());
    }

    @Override
    public List<Relation> getRelations(Kingdom kingdom, Kingdom other) {
        return relations.stream().filter(r -> (r.kingdom.equals(kingdom) && r.otherKingdom.equals(other))
                || (r.kingdom.equals(other) && r.otherKingdom.equals(kingdom))).collect(Collectors.toList());
    }

    @Override
    public void watch(Relation relation) {
        if ( !relations.contains(relation) ) {
            relations.add((BRelation) relation);
        }

        fit(relation);
    }

    @Override
    public void unwatch(Relation relation) {
        relations.remove(relation);
    }

    @Override
    public void fit(Relation relation) {
        BRelation brel = (BRelation) relation;
        brel.kingdom = (BKingdom) getKingdom(brel.kingdom.getName());
        brel.otherKingdom = (BKingdom) getKingdom(brel.otherKingdom.getName());
    }


    // users

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public User getUser(String name) {
        return users.stream().filter(u -> u.name.equals(name)).findFirst().orElse(null);
    }

    @Override
    public User getUser(UUID id) {
        return users.stream().filter(u -> u.id.equals(id.toString())).findFirst().orElse(null);
    }

    @Override
    public void watch(User user) {
        if ( !users.contains(user) ) {
            users.add((BUser) user);
        }

        fit(user);
    }

    @Override
    public void unwatch(User user) {
        users.remove(user);
    }

    @Override
    public void fit(User user) {
        BUser buser = (BUser) user;
        if ( buser.kingdom != null ) {
            buser.kingdom = (BKingdom) getKingdom(buser.kingdom.getName());

            if ( buser.rank != null ){
                buser.rank = (BRank) buser.kingdom.getRank(buser.rank.getName());
            }
        }
    }
}
