package com.gufli.kingdomcraft.api.editor;

@FunctionalInterface
public interface EditorAttributeValidator {
    boolean validate(String value);
}