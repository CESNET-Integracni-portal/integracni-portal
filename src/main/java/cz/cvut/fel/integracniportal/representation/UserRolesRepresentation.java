package cz.cvut.fel.integracniportal.representation;

import java.util.List;

/**
 * @author Radek Jezdik
 */
public class UserRolesRepresentation {

    private List<String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
