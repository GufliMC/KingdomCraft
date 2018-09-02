package com.igufguf.kingdomcraft.api.handlers;

import com.igufguf.kingdomcraft.api.exceptions.UserNotFoundException;
import com.igufguf.kingdomcraft.api.models.kingdom.Kingdom;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomRank;
import com.igufguf.kingdomcraft.api.models.kingdom.KingdomUser;
import org.bukkit.entity.Player;

import java.util.List;

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
     * Register a new user to the handler
     *
     * @param user The User to register
     */
    void registerUser(KingdomUser user);

    /**
     * Unregister a user from the handler
     *
     * @param user The User to unregister
     */
    void unregisterUser(KingdomUser user);

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
     * Get the user object for the given uuid or name (name if uuid is not found)
     *
     * If the user exists, all data will be loaden into the user. If not, a standard user will be returned.
     *
     * @param uuid
     * @param name
     * @return The KingdomUser for the given parameters
     */
    KingdomUser getOfflineUser(String uuid, String name);

    /**
     * Get the user object for the given uuid
     *
     * If the user exists, all data will be loaden into the user.
     * If not, a UserNotFoundException will be thrown.
     *
     * @param uuid
     * @throws UserNotFoundException
     * @return The existing KingdomUser for the given uuid
     */
    KingdomUser getOfflineUser(String uuid) throws UserNotFoundException;

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
}
