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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

        UserDetails specialUserDetails = resolveSpecialUser(loggedUser);

        if (specialUserDetails != null) {
            return specialUserDetails;
        } else {
            return getUserByUsername(loggedUser.getUsername());
        }
    }

    private UserDetails resolveSpecialUser(User loggedUser) {
        try {
            org.springframework.security.core.userdetails.UserDetails adminDetails = adminUserService.loadUserByUsername(loggedUser.getUsername());

            UserDetails userDetails = new UserDetails();
            userDetails.setId("0");
            userDetails.setUsername(adminDetails.getUsername());

            Set<Permission> permissions = new HashSet<Permission>();
            permissions.addAll(Arrays.asList(Permission.values()));
            userDetails.setPermissions(permissions);

            return userDetails;

        } catch (UsernameNotFoundException e) {
            return null;
        }
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
                "test@email.cz"));

        return getUserById(userId);
    }

    @Override
    public void changePassword(String userId, String newPassword, String oldPassword) {

        // TODO
//
//        commandGateway.sendAndWait(new SetUserPasswordCommand(
//                UserId.of(userId),
//                userDetailsRepresentation.getUsername(),
//                "test@email.cz"));


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
