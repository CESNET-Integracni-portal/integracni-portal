package cz.cvut.fel.integracniportal.domain.user.events;

import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Value
public class UserPermissionsUpdatedEvent {

    public final UserId userId;

    public final Set<Permission> addedPermissions;

    public final Set<Permission> removedPermissions;

    public UserPermissionsUpdatedEvent(UserId userId, Set<Permission> addedPermissions, Set<Permission> removedPermissions) {
        this.userId = userId;
        this.addedPermissions = Collections.unmodifiableSet(addedPermissions);
        this.removedPermissions = Collections.unmodifiableSet(removedPermissions);
    }
}
