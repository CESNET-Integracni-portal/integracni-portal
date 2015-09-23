package cz.cvut.fel.integracniportal.domain.user.events;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class UserCreatedEvent {

    private final UserId id;

    private final String username;

    private final String email;

    public UserCreatedEvent(UserId id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

}
