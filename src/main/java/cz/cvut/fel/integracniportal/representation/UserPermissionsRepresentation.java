package cz.cvut.fel.integracniportal.representation;

import java.util.List;

/**
 * @author Radek Jezdik
 */
public class UserPermissionsRepresentation {

    private List<String> permissions;

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
