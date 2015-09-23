package cz.cvut.fel.integracniportal.query.user;

import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.dao.UserRoleDao;
import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.user.events.UserCreatedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPermissionsUpdatedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserRolesUpdatedEvent;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Component
public class UserUpdater {

    @Autowired
    private UserDetailsDao userDao;

    @Autowired
    private UserRoleDao roleDao;

    @EventHandler
    public void handle(UserCreatedEvent event) {
        UserDetails user = new UserDetails();

        user.setId(event.getId().getId());
        user.setUsername(event.getUsername());
        user.setEmail(event.getEmail());

        userDao.save(user);
    }

    @EventHandler
    public void handle(UserRolesUpdatedEvent event) {
        UserDetails user = userDao.getReference(event.getUserId().getId());

        List<UserRole> userRoles = user.getUserRoles();
        if (userRoles == null) {
            userRoles = new ArrayList<UserRole>();
            user.setUserRoles(userRoles);
        }

        for (UserRoleId roleId : event.getAddedRoleIds()) {
            UserRole role = roleDao.load(roleId.getId());
            userRoles.add(role);
        }

        for (UserRoleId roleId : event.getRemovedRoleIds()) {
            UserRole role = roleDao.load(roleId.getId());
            userRoles.remove(role);
        }

        userDao.save(user);
    }

    @EventHandler
    public void handle(UserPermissionsUpdatedEvent event) {
        UserDetails user = userDao.getReference(event.getUserId().getId());

        Set<Permission> permissions = user.getPermissions();
        if (permissions == null) {
            permissions = new HashSet<Permission>();
            user.setPermissions(permissions);
        }

        permissions.addAll(event.getAddedPermissions());
        permissions.removeAll(event.getRemovedPermissions());

        userDao.save(user);
    }

}
