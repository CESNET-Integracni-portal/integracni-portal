package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.userrole.CreateUserRoleCommand;
import cz.cvut.fel.integracniportal.command.userrole.UpdateUserRoleCommand;
import cz.cvut.fel.integracniportal.dao.UserRoleDao;
import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.representation.UserRoleRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link cz.cvut.fel.integracniportal.service.UserRoleService}.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private CommandGateway commandGateway;

    @Transactional(readOnly = true)
    @Override
    public UserRole getRoleById(String id) {
        UserRole userRole = userRoleDao.get(id);
        if (userRole == null) {
            throw new NotFoundException("role.notFound", id);
        }
        return userRole;
    }

    @Transactional(readOnly = true)
    @Override
    public UserRole getRoleByName(String name) {
        return userRoleDao.getRoleByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserRole> getAllRoles() {
        return userRoleDao.getAllRoles();
    }

    @Override
    public UserRole createRole(UserRoleRepresentation role) {
        String id = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new CreateUserRoleCommand(
                UserRoleId.of(id),
                role.getName(),
                role.getDescription(),
                new HashSet<Permission>(role.getPermissions())
        ));

        return userRoleDao.get(id);
    }

    @Override
    public void updateRole(String roleId, UserRoleRepresentation userRoleRepresentation) {
        commandGateway.sendAndWait(new UpdateUserRoleCommand(
                UserRoleId.of(roleId),
                userRoleRepresentation.getName(),
                userRoleRepresentation.getDescription(),
                new HashSet<Permission>(userRoleRepresentation.getPermissions())
        ));
    }

}
