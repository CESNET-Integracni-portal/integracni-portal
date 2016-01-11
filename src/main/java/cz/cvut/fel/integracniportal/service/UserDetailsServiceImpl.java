package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.command.user.CreateUserCommand;
import cz.cvut.fel.integracniportal.command.user.SetUserPermissionsCommand;
import cz.cvut.fel.integracniportal.command.user.SetUserRolesCommand;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.domain.Permission;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the {@link cz.cvut.fel.integracniportal.service.UserDetailsService}.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserDetailsDao userDao;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    @Qualifier("adminUserService")
    private org.springframework.security.core.userdetails.UserDetailsService adminUserService;

    @Override
    @Transactional
    public UserDetails getUserById(String userId) {
        UserDetails userDetails = userDao.getUserById(userId);
        if (userDetails == null) {
            throw new NotFoundException("user.notFound.id", userId);
        }
        return userDetails;
    }

    @Override
    @Transactional
    public UserDetails getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    /**
     * TODO admin není v db
     * FIXME admin není v db
     *
     * @return
     */
    @Override
    @Transactional
    public UserDetails getCurrentUser() {
        Authentication authentication = authenticationService.getCurrentAuthentication();
        User loggedUser = (User) authentication.getPrincipal();
        if (loggedUser == null) {
            return null;
        }

        return getUserByUsername(loggedUser.getUsername());
    }


    @Override
    @Transactional
    public List<UserDetails> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    @Transactional
    public List<UserDetails> getAllUsersInOrganizationalUnit(String organizationalUnitId) {
        return userDao.getAllUsersInOrganizationalUnit(organizationalUnitId);
    }

    @Override
    public UserDetails createUser(UserDetailsRepresentation userDetailsRepresentation) {
        String userId = UUID.randomUUID().toString();

        commandGateway.sendAndWait(new CreateUserCommand(
                UserId.of(userId),
                userDetailsRepresentation.getUsername(),
                "test@email.cz")); // FIXME real email

        return getUserById(userId);
    }

    @Override
    public void changePassword(String userId, String newPassword, String oldPassword) {

        // TODO - put on hold, because of email infrastructure

//        commandGateway.sendAndWait(new SetUserPasswordCommand(
//                token,
//                password);


    }

    @Override
    public void updateRoles(String userId, List<String> roles) {
        Set<UserRoleId> roleIds = new HashSet<UserRoleId>();

        for (String role : roles) {
            roleIds.add(UserRoleId.of(role));
        }

        commandGateway.sendAndWait(new SetUserRolesCommand(
                UserId.of(userId),
                roleIds
        ));
    }

    @Override
    public void updatePermissions(String userId, List<String> permissions) {
        Set<Permission> permissionSet = new HashSet<Permission>();

        for (String permission : permissions) {
            permissionSet.add(Permission.create(permission));
        }

        commandGateway.sendAndWait(new SetUserPermissionsCommand(
                UserId.of(userId),
                permissionSet
        ));
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setUserDao(UserDetailsDao userDao) {
        this.userDao = userDao;
    }

}
