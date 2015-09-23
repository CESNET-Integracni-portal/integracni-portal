package cz.cvut.fel.integracniportal.domain.user.events;

import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Value
public class UserRolesUpdatedEvent {

    public final UserId userId;

    public final Set<UserRoleId> addedRoleIds;

    public final Set<UserRoleId> removedRoleIds;

    public UserRolesUpdatedEvent(UserId userId, Set<UserRoleId> addedRoleIds, Set<UserRoleId> removedRoleIds) {
        this.userId = userId;
        this.addedRoleIds = Collections.unmodifiableSet(addedRoleIds);
        this.removedRoleIds = Collections.unmodifiableSet(removedRoleIds);
    }
}
