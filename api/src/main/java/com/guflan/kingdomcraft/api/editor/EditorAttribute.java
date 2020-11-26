/*
 * This file is part of KingdomCraft.
 *
 * KingdomCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KingdomCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with KingdomCraft. If not, see <https://www.gnu.org/licenses/>.
 */

package com.guflan.kingdomcraft.api.editor;

public class EditorAttribute {

    private final String name;
    private final String description;
    private final EditorAttributeValidator validator;

    public EditorAttribute(String name, String description, EditorAttributeValidator validator) {
        this.name = name;
        this.description = description;
        this.validator = validator;
    }

    public EditorAttribute(String name, String description) {
        this(name, description, null);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public boolean validate(String value) {
        return validator == null || validator.validate(value);
    }

    @FunctionalInterface
    public interface EditorAttributeValidator {
        boolean validate(String value);
    }
}
