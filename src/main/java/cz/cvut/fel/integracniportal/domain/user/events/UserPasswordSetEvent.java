package cz.cvut.fel.integracniportal.domain.user.events;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class UserPasswordSetEvent {

    public final UserId userId;

}