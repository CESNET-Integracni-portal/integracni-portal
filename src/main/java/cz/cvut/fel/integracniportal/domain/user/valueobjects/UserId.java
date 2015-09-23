package cz.cvut.fel.integracniportal.domain.user.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class UserId {

    private String id;

    public UserId(String id) {
        this.id = id;
    }

    public static UserId of(String id) {
        return new UserId(id);
    }

}
