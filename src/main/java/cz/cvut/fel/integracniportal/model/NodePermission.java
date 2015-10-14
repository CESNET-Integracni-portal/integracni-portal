package cz.cvut.fel.integracniportal.model;


import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Filesystem nodes permissions enumeration.
 *
 * @author Eldar Iosip
 */
public enum NodePermission {
    READ("read"),
    EDIT("edit"),
    SHARE("share"),
    DOWNLOAD("download"),
    UPLOAD("upload");

    private String name;

    NodePermission(String name) {
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
}
