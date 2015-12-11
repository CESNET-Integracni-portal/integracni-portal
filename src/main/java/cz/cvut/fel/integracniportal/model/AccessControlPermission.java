package cz.cvut.fel.integracniportal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import cz.cvut.fel.integracniportal.exceptions.PermissionNotFoundException;

/**
 * @author Eldar Iosip
 */
public enum AccessControlPermission {

    READ("READ"),
    EDIT("EDIT"),
    SHARE("SHARE"),
    DOWNLOAD("DOWNLOAD"),
    UPLOAD("UPLOAD"),
    EDIT_PERMISSIONS("EDIT_PERMISSIONS");

    private String name;

    AccessControlPermission(String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    /**
     * TODO: add doc
     */
    @JsonCreator
    public static AccessControlPermission create(String name) throws PermissionNotFoundException {
        AccessControlPermission permission;
        try {
            permission = AccessControlPermission.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new PermissionNotFoundException("permission.notFound", name);
        }
        return permission;
    }
}