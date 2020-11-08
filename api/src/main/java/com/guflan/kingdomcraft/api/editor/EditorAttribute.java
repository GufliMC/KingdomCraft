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
