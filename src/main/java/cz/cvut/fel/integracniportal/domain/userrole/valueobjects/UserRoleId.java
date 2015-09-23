package cz.cvut.fel.integracniportal.domain.userrole.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class UserRoleId {

    private String id;

    public UserRoleId(String id) {
        this.id = id;
    }

    public static UserRoleId of(String id) {
        return new UserRoleId(id);
    }

}
