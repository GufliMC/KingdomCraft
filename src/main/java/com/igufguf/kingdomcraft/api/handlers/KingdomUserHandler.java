package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.exceptions.UserNotFoundException;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import com.igufguf.kingdomcraft.utils.KingdomUtils;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public interface KingdomUserHandler {

    /**
     * Get all online users
     *
     * @return A list of online users
     */
    List<KingdomUser> getUsers();

    /**
     * Get all existing users
     *
     * @return A list of existing users
     */
    List<KingdomUser> getAllUsers();

    /**
     * Get a kingdom user by a player
     *
     * @param p Player
     * @return The KingdomUser for the given Player
     */
    KingdomUser getUser(Player p);

    /**
     * Get a kingdom user by username
     *
     * @param username Name of the user
     * @return The KingdomUser for the given name
     */
    KingdomUser getUser(String username);

    /**
     * Get the user object for the given uuid or name
     *
     * If the user exists, all data will be loaden into the user.
     *
     * @param uuid
     * @return The existing KingdomUser for the given parameters or null
     */
    KingdomUser getOfflineUser(UUID uuid, String name);

    /**
     * Load a user into the handler
     *
     * @param player
     * @return The KingdomUser for this player
     */
    KingdomUser loadUser(Player player);

    /**
     * Unload a user from the handler
     *
     * @param user The User to unload
     */
    void unloadUser(KingdomUser user);

    /**
     * Get the kingdom of a user
     *
     * @param user
     * @return The Kingdom this user is member of or null
     */
    Kingdom getKingdom(KingdomUser user);

    /**
     * Get the rank of a user
     *
     * @param user
     * @return The KingdomRank of this user or null
     */
    KingdomRank getRank(KingdomUser user);

    /**
     * Change the kingdom of a user or make a user leave
     *
     * @param user The user to change
     * @param kingdom The new Kingdom or null
     */
    void setKingdom(KingdomUser user, Kingdom kingdom);

    /**
     * Change the rank of a user
     *
     * @param user The user to change
     * @param rank The new Rank for this user
     */
    void setRank(KingdomUser user, KingdomRank rank);

    /**
     * Make a user the default rank for their kingdom
     *
     * @param user The user to change
     */
    void setDefaultRank(KingdomUser user);

    /**
     * Save a user in the database
     *
     * @param user The KingdomUser to save
     */
    void save(KingdomUser user);


    // api, set & get custom user values

    void setUserData(KingdomUser user, String path, Object value);
    Object getUserData(KingdomUser user, String path);
}
