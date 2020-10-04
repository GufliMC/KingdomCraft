package com.guflan.kingdomcraft.api.managers;


import com.guflan.kingdomcraft.api.domain.Kingdom;
import com.guflan.kingdomcraft.api.domain.User;

import java.util.List;
import java.util.UUID;

public interface UserManager {

    List<User> getOnlineUsers();

    List<User> getUsers();

    User loadUser(UUID uuid, String name);

    User getUser(String name);

    User getUser(UUID uuid);

    /*
    void joinKingdom(User user, Kingdom kingdom);

    void leaveKingdom(User user);

    void addInvite(User from, User target);
     */

    void saveUser(User player);

}
