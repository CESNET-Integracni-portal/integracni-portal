package cz.cvut.fel.integracniportal.domain.group.events;

import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

/**
 * @author Radek Jezdik
 */
@Value
public class UserAddedToGroupEvent {

    private final GroupId id;

    private final UserId user;

}
