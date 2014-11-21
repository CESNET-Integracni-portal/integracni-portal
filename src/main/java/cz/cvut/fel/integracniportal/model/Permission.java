package cz.cvut.fel.integracniportal.model;


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

    @Override
    public String toString() {
        return name;
    }
}
