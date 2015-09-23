package cz.cvut.fel.integracniportal.domain.userrole.events;

import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Value;

import java.util.Collections;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Value
public class UserRolePermissionsUpdatedEvent {

    private final UserRoleId id;

    private final Set<Permission> addedPermissions;

    private final Set<Permission> removedPermissions;

    public UserRolePermissionsUpdatedEvent(UserRoleId id, Set<Permission> addedPermissions, Set<Permission> removedPermissions) {
        this.id = id;
        this.addedPermissions = Collections.unmodifiableSet(addedPermissions);
        this.removedPermissions = Collections.unmodifiableSet(removedPermissions);
    }
}
