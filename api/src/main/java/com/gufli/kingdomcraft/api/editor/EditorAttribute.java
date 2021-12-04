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

package com.gufli.kingdomcraft.api.editor;

public class EditorAttribute {

    public static Builder builder(String key, String display) {
        return new Builder(key, display);
    }

    private final String key;
    private final String display;
    private final String group;
    private final String helpLink;
    private final EditorAttributeType type;
    private final EditorAttributeValidator validator;

    private EditorAttribute(String key, String display, String group, String helpLink, EditorAttributeType type, EditorAttributeValidator validator) {
        this.key = key;
        this.display = display;
        this.group = group;
        this.validator = validator;
        this.type = type;
        this.helpLink = helpLink;
    }

    public String key() {
        return key;
    }

    public String display() {
        return display;
    }

    public String group() {
        return group;
    }

    public String helpLink() {
        return helpLink;
    }

    public EditorAttributeType type() {
        return type;
    }

    public boolean validate(String value) {
        return validator == null || validator.validate(value);
    }

    public static class Builder {

        private final String key;
        private final String display;

        private String group;
        private String helpLink;
        private EditorAttributeType type;
        private EditorAttributeValidator validator;

        private Builder(String key, String display) {
            if ( key == null ) {
                throw new IllegalArgumentException("Key cannot be null.");
            }
            if ( display == null ) {
                throw new IllegalArgumentException("Display cannot be null.");
            }
            this.key = key;
            this.display = display;
        }

        public Builder withValidator(EditorAttributeValidator validator) {
            this.validator = validator;
            return this;
        }

        public Builder withType(EditorAttributeType type) {
            this.type = type;
            if ( type != null && this.validator == null ) {
                this.validator = type.validator();
            }
            return this;
        }

        public Builder withGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder withHelpLink(String helpLink) {
            this.helpLink = helpLink;
            return this;
        }

        public EditorAttribute build() {
            return new EditorAttribute(key, display, group, helpLink, type, validator);
        }

    }
}
