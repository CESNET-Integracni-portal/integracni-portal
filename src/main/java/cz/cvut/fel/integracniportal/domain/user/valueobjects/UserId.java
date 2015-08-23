package cz.cvut.fel.integracniportal.domain.user.valueobjects;

import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value(staticConstructor = "of")
public class UserId {

    private Long id;

    public UserId(Long id) {
        this.id = id;
    }

    public static UserId of(Long id) {
        return new UserId(id);
    }

}
