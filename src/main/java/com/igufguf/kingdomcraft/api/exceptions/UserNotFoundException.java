package com.igufguf.kingdomcraft.api.exceptions;

/**
 * Created by Joris on 17/08/2018 in project KingdomCraft.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String uuid) {
        super("A user with uuid '" + uuid + "' does not exist!");
    }

}
