package cz.cvut.fel.integracniportal.domain.userrole;

import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.domain.userrole.events.UserRoleCreatedEvent;
import cz.cvut.fel.integracniportal.domain.userrole.events.UserRoleDetailsUpdatedEvent;
import cz.cvut.fel.integracniportal.domain.userrole.events.UserRolePermissionsUpdatedEvent;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.ObjectUtils.notEqual;

/**
 * @author Radek Jezdik
 */
@NoArgsConstructor
@Getter
public class UserRole extends AbstractAnnotatedAggregateRoot<UserId> {

    @AggregateIdentifier
    private UserRoleId id;

    private String name;

    private String description;

    private Set<Permission> permissions = new HashSet<Permission>();

    public UserRole(UserRoleId id, String name, String description, Set<Permission> permissions) {
        apply(new UserRoleCreatedEvent(id, name, description));
        apply(new UserRolePermissionsUpdatedEvent(id, permissions, Collections.<Permission>emptySet()));
    }

    public void update(String name, String description, Set<Permission> permissions) {
        updateDetails(name, description);
        updatePermissions(permissions);
    }

    private void updateDetails(String newName, String newDescription) {
        if (detailsChanged(newName, newDescription)) {
            apply(new UserRoleDetailsUpdatedEvent(id, newName, newDescription));
        }
    }

    private boolean detailsChanged(String newName, String newDescription) {
        return notEqual(this.name, newName)
                && notEqual(this.description, newDescription);
    }

    private void updatePermissions(Set<Permission> newPermissions) {
        Set<Permission> removedPermissions = new HashSet<Permission>(this.permissions);
        Set<Permission> addedPermissions = new HashSet<Permission>();

        for (Permission permission : newPermissions) {
            if (this.permissions.contains(permission)) {
                removedPermissions.remove(permission);
            } else {
                addedPermissions.add(permission);
            }
        }

        if (addedPermissions.isEmpty() == false && removedPermissions.isEmpty() == false) {
            apply(new UserRolePermissionsUpdatedEvent(id, addedPermissions, removedPermissions));
        }
    }

    @EventSourcingHandler
    public void handle(UserRoleCreatedEvent event) {
        id = event.getId();
        name = event.getName();
        description = event.getDescription();
    }

    @EventSourcingHandler
    public void handle(UserRolePermissionsUpdatedEvent event) {
        permissions.addAll(event.getAddedPermissions());
        permissions.removeAll(event.getRemovedPermissions());
    }

}
