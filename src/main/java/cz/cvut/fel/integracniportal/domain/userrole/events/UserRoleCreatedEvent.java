package cz.cvut.fel.integracniportal.domain.userrole.events;

import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class UserRoleCreatedEvent {

    private final UserRoleId id;

    private final String name;

    private final String description;

}
