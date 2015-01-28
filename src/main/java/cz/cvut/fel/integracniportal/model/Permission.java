package cz.cvut.fel.integracniportal.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.cvut.fel.integracniportal.exceptions.PermissionNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Permissions enumeration.
 */
public enum Permission {

    /**
     * The MAIN_ADMIN.
     */
    MAIN_ADMIN("main_admin", true),
    /**
     * Permission to edit organizational units.
     */
    EDIT_ORGANIZATIONAL_UNITS("units", false),

    /**
     * Permission to edit externists.
     */
    EDIT_EXTERNISTS("externists", true),

    /**
     * Permission to change my own password.
     */
    CHANGE_PASSWORD("password", true);

    private final String name;

    private final Boolean roleAssignable;

    private Permission(String name, Boolean roleAssignable) {
        this.name = name;
        this.roleAssignable = roleAssignable;
    }

    /**
     * Indicates, whether the permission can be assigned to a role.
     *
     * @return boolean representing whether this permission is role assignable
     */
    public Boolean isRoleAssignable() {
        return roleAssignable;
    }


    /**
     * Method used for serialization of the permission.
     *
     * @return serialized string representing the permission
     */
    @JsonValue
    @Override
    public String toString() {
        return name;
    }

    /**
     * Create permission.
     *
     * @param name name of the permission
     * @return the permission
     * @throws PermissionNotFoundException indicates that the permission does not exist
     */
    @JsonCreator
    public static Permission create(String name) throws PermissionNotFoundException {
        Permission permission = permissions.get(name);
        if (permission == null) {
            throw new PermissionNotFoundException("permission.notFound", name);
        }
        return permission;
    }

    private static Map<String, Permission> permissions = Permission.initPermissionMap();

    private static Map<String, Permission> initPermissionMap() {
        Map<String, Permission> result = new HashMap<String, Permission>();
        for (Permission permission : Permission.values()) {
            result.put(permission.toString(), permission);
        }
        return result;
    }
}
