package cz.cvut.fel.integracniportal.domain.user;

import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.user.entities.LocalUserType;
import cz.cvut.fel.integracniportal.domain.user.entities.UserType;
import cz.cvut.fel.integracniportal.domain.user.events.UserCreatedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPermissionsUpdatedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserRolesUpdatedEvent;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.axonframework.eventsourcing.annotation.EventSourcedMember;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@NoArgsConstructor
@Getter
public class User extends AbstractAnnotatedAggregateRoot<UserId> {

    @AggregateIdentifier
    private UserId id;

    private String username;

    private String email;

    @EventSourcedMember
    private UserType userType;

    private Set<UserRoleId> roles = new HashSet<UserRoleId>();

    private Set<Permission> directPermissions = new HashSet<Permission>();

    public User(UserId id, String username, String email, @NotNull Set<UserRoleId> roles) {
        apply(new UserCreatedEvent(id, username, email));

        if (roles.size() > 0) {
            apply(new UserRolesUpdatedEvent(id, roles, Collections.<UserRoleId>emptySet()));
        }

        startVerificationReset();
    }

    public void startVerificationReset() {
        userType.startVerification(this);
    }

    public void startPasswordReset() {
        userType.startPasswordReset(this);
    }

    public void setPassword() {
        userType.setPassword(this);
    }

    public void setRoles(Set<UserRoleId> newRoles) {
        Set<UserRoleId> removedRoles = new HashSet<UserRoleId>(this.roles);
        Set<UserRoleId> addedRoles = new HashSet<UserRoleId>();

        for (UserRoleId roleId : newRoles) {
            if (this.roles.contains(roleId)) {
                removedRoles.remove(roleId);
            } else {
                addedRoles.add(roleId);
            }
        }

        apply(new UserRolesUpdatedEvent(id, addedRoles, removedRoles));
    }

    public void setPermissions(Set<Permission> newPermissions) {
        Set<Permission> removedPermissions = new HashSet<Permission>(this.directPermissions);
        Set<Permission> removedRoles = new HashSet<Permission>();

        for (Permission permission : newPermissions) {
            if (this.directPermissions.contains(permission)) {
                removedPermissions.remove(permission);
            } else {
                removedRoles.add(permission);
            }
        }

        apply(new UserPermissionsUpdatedEvent(id, removedRoles, removedPermissions));
    }

    @EventSourcingHandler
    public void handle(UserCreatedEvent event) {
        id = event.getId();
        username = event.getUsername();
        email = event.getEmail();
        userType = new LocalUserType();
    }

    @EventSourcingHandler
    public void handle(UserRolesUpdatedEvent event) {
        this.roles.addAll(event.getAddedRoleIds());
        this.roles.removeAll(event.getRemovedRoleIds());
    }

    @EventSourcingHandler
    public void handle(UserPermissionsUpdatedEvent event) {
        this.directPermissions.addAll(event.getAddedPermissions());
        this.directPermissions.removeAll(event.getRemovedPermissions());
    }

}
