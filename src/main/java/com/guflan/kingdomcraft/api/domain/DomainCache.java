package com.guflan.kingdomcraft.api.domain;

import com.guflan.kingdomcraft.api.domain.models.Kingdom;
import com.guflan.kingdomcraft.api.domain.models.Relation;
import com.guflan.kingdomcraft.api.domain.models.User;

import java.util.List;
import java.util.UUID;

public interface DomainCache {

    List<Kingdom> getKingdoms();

    Kingdom getKingdom(String name);

    void watch(Kingdom kingdom);

    void unwatch(Kingdom kingdom);

    // relations

    List<Relation> getRelations();

    List<Relation> getRelations(Kingdom kingdom);

    List<Relation> getRelations(Kingdom kingdom, Kingdom other);

    void watch(Relation relation);

    void unwatch(Relation relation);

    void fit(Relation relation);

    // users

    List<User> getUsers();

    User getUser(String name);

    User getUser(UUID id);

    void watch(User user);

    void unwatch(User user);

    void fit(User user);
}
