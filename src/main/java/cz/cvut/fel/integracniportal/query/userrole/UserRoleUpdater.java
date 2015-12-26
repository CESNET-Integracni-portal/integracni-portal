package cz.cvut.fel.integracniportal.query.userrole;

import cz.cvut.fel.integracniportal.dao.UserRoleDao;
import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.userrole.events.UserRoleCreatedEvent;
import cz.cvut.fel.integracniportal.domain.userrole.events.UserRoleDetailsUpdatedEvent;
import cz.cvut.fel.integracniportal.domain.userrole.events.UserRolePermissionsUpdatedEvent;
import cz.cvut.fel.integracniportal.model.UserRole;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Radek Jezdik
 */
@Component
public class UserRoleUpdater {

    @Autowired
    private UserRoleDao roleDao;

    @EventHandler
    public void handle(UserRoleCreatedEvent event) {
        UserRole role = new UserRole();

        role.setId(event.getId().getId());
        role.setName(event.getName());
        role.setDescription(event.getDescription());

        roleDao.save(role);
    }

    @EventHandler
    public void handle(UserRoleDetailsUpdatedEvent event) {
        UserRole role = roleDao.get(event.getId().getId());

        role.setName(event.getName());
        role.setDescription(event.getDescription());
    }

    @EventHandler
    public void handle(UserRolePermissionsUpdatedEvent event) {
        UserRole role = roleDao.get(event.getId().getId());

        Set<Permission> permissions = role.getPermissions();

        if (permissions == null) {
            permissions = new HashSet<Permission>();
            role.setPermissions(permissions);
        }

        for (Permission permission : event.getAddedPermissions()) {
            permissions.add(permission);
        }
        for (Permission permission : event.getRemovedPermissions()) {
            permissions.remove(permission);
        }
    }

}
