package com.igufguf.kingdomcraft.api.models.flags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
public class KingdomFlag<T> {

    public static final KingdomFlag<Boolean> INVITE_ONLY = new KingdomFlag<>("invite-only", Boolean.class);
    public static final KingdomFlag<Boolean> FRIENDLYFIRE = new KingdomFlag<>("friendlyfire", Boolean.class);

    protected final String name;
    protected final Class<?> type;

    public KingdomFlag(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * Validate if an object matches the type of this KingdomFlag
     *
     * @param value The value to validate
     * @return true or false
     */
    public boolean validate(Object value) {
        if ( value == null ) throw new IllegalArgumentException("Value cannot be null.");
        return type.isAssignableFrom(value.getClass());
    }

    /**
     * Parse an input to the type of the KingdomFlag
     *
     * @param value The value to parse
     * @throws ClassCastException if the value cannot be parsed to this type
     * @return The value of this type
     */
    public T parse(Object value) throws ClassCastException {
        if ( validate(value) ) return (T) value;

        // java default valueOf for Boolean is odd
        if ( type == Boolean.class ) {
            if ( !(value instanceof String) )
                throw new ClassCastException(value.getClass().getName() + " cannot be cast to " + type.getName());

            if ( ((String) value).equalsIgnoreCase("true") ) return (T) new Boolean(true);
            else if ( ((String) value).equalsIgnoreCase("false") ) return (T) new Boolean(false);

            throw new ClassCastException(value.getClass().getName() + " cannot be cast to " + type.getName());
        }

        // valueOf method does not exist on primitives, we give this an extra warning for developers
        if ( type.isPrimitive() )
            throw new IllegalStateException("The type of this KingdomFlag is a primitive, this cannot be parsed!");

        // some objects only have a valueOf(String) method
        if ( value instanceof String ) {
            try {
                Method valueOf = type.getMethod("valueOf", String.class);
                return (T) valueOf.invoke(type, value);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                ignored.printStackTrace();
            }
        }

        // defaults to other objects valueOf(Object) method
        try {
            Method valueOf = type.getMethod("valueOf", Object.class);
            return (T) valueOf.invoke(type, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            ignored.printStackTrace();
        }

        // valueOf method does not exist on this type, developer should extend KingdomFlag and override this method
        throw new ClassCastException(value.getClass().getName() + " cannot be cast to " + type.getName());
    }

}
