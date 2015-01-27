package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.PermissionNotFoundException;
import cz.cvut.fel.integracniportal.exceptions.UserRoleNotFoundException;
import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    private UserRoleService userRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails getUserById(long userId) throws NotFoundException {
        UserDetails userDetails = userDao.getUserById(userId);
        if (userDetails == null) {
            throw new NotFoundException("user.notFound.id", userId);
        }
        return userDetails;
    }

    @Override
    public UserDetails getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Override
    public UserDetails getCurrentUser() {
        Authentication authentication = authenticationService.getCurrentAuthentication();
        User loggedUser = (User) authentication.getPrincipal();
        if (loggedUser == null) {
            return null;
        }
        UserDetails userDetails = getUserByUsername(loggedUser.getUsername());
        return userDetails;
    }

    @Override
    public List<UserDetails> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public List<UserDetails> getAllUsersInOrganizationalUnit(Long organizationalUnitId) {
        return userDao.getAllUsersInOrganizationalUnit(organizationalUnitId);
    }

    @Override
    @Transactional
    public UserDetails createUser(UserDetailsRepresentation userDetailsRepresentation) throws UserRoleNotFoundException, AlreadyExistsException, PermissionNotFoundException {
        if (getUserByUsername(userDetailsRepresentation.getUsername()) != null) {
            throw new AlreadyExistsException("user.alreadyExists");
        }

        UserDetails user = new UserDetails();
        updateUserFromRepresentation(user, userDetailsRepresentation);
        userDao.save(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = {UserRoleNotFoundException.class, NotFoundException.class})
    public UserDetails updateUser(Long userId, UserDetailsRepresentation userDetailsRepresentation) throws UserRoleNotFoundException, NotFoundException, PermissionNotFoundException {
        UserDetails userDetails = getUserById(userId);
        updateUserFromRepresentation(userDetails, userDetailsRepresentation);
        userDao.save(userDetails);
        return userDetails;
    }

    private void updateUserFromRepresentation(UserDetails userDetails, UserDetailsRepresentation userDetailsRepresentation) throws PermissionNotFoundException, UserRoleNotFoundException {
        if (userDetailsRepresentation.getUsername() != null) {
            userDetails.setUsername(userDetailsRepresentation.getUsername());
        }
        if (userDetailsRepresentation.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userDetailsRepresentation.getPassword());
            userDetails.setPassword(encodedPassword);
        }
        if (userDetailsRepresentation.getUnitId() != null) {
            userDetails.setOrganizationalUnitId(userDetailsRepresentation.getUnitId());
        }
        if (userDetailsRepresentation.getDirectPermissions() != null) {
            if (userDetails.getPermissions() == null) {
                userDetails.setPermissions(new HashSet<Permission>());
            } else {
                userDetails.getPermissions().clear();
            }
            for (String permissionName: userDetailsRepresentation.getDirectPermissions()) {
                Permission permission = Permission.create(permissionName);
                userDetails.getPermissions().add(permission);
            }
        }
        if (userDetailsRepresentation.getRoles() != null) {
            if (userDetails.getUserRoles() == null) {
                userDetails.setUserRoles(new ArrayList<UserRole>());
            } else {
                userDetails.getUserRoles().clear();
            }
            for (String roleName: userDetailsRepresentation.getRoles()) {
                UserRole role = userRoleService.getRoleByName(roleName);
                if (role == null) {
                    throw new UserRoleNotFoundException("role.notFound", roleName);
                }
                userDetails.getUserRoles().add(role);
            }
        }
    }

    @Override
    public void saveUser(UserDetails user) {
        userDao.save(user);
    }

    @Override
    public void removeUser(UserDetails user) {
        userDao.delete(user);
    }

    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void setUserDao(UserDetailsDao userDao) {
        this.userDao = userDao;
    }

    public void setUserRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
