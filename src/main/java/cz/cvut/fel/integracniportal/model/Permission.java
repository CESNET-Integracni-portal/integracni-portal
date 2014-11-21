package cz.cvut.fel.integracniportal.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.HashMap;
import java.util.Map;

/**
 * Permissions enumeration.
 */
public enum Permission {

    /**
     * Permission to edit organizational units.
     */
    EDIT_ORGANIZATIONAL_UNITS("units"),

    /**
     * Permission to edit externists.
     */
    EDIT_EXTERNISTS("externists"),

    /**
     * Permission to change my own password.
     */
    CHANGE_PASSWORD("password");

    private final String name;

    private Permission(String name) {
        this.name = name;
    }

    //Serialization
    @JsonValue
    @Override
    public String toString() {
        return name;
    }

    // Deserialization
    @JsonCreator
    public static Permission create(String name) throws JsonMappingException {
        Permission permission = permissions.get(name);
        if (permission == null) {
            throw new JsonMappingException("permission.notFound");
        }
        return permission;
    }

    private static Map<String, Permission> permissions = Permission.initPermissionMap();

    private static Map<String, Permission> initPermissionMap() {
        Map<String, Permission> result = new HashMap<String, Permission>();
        for (Permission permission: Permission.values()) {
            result.put(permission.toString(), permission);
        }
        return result;
    }
}
