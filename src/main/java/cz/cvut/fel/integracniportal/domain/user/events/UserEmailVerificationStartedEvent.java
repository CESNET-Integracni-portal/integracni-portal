package cz.cvut.fel.integracniportal.domain.user.events;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class UserEmailVerificationStartedEvent {

    private final UserId userId;

}
