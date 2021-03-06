package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.OrganizationalUnitDao;
import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.InvalidStateException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.UserRoleNotFoundException;
import cz.cvut.fel.integracniportal.model.OrganizationalUnit;
import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Implementation of the {@link cz.cvut.fel.integracniportal.service.UserDetailsService}.
 */
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserDetailsDao userDao;

    @Autowired
    private OrganizationalUnitDao orgUnitDao;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("adminUserService")
    private org.springframework.security.core.userdetails.UserDetailsService adminUserService;

    @Override
    public UserDetails getUserById(long userId) {
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

    /**
     * TODO admin není v db
     * FIXME admin není v db
     *
     * @return
     */
    @Override
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
            userDetails.setId(0L);
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
    public List<UserDetails> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public List<UserDetails> getAllUsersInOrganizationalUnit(Long organizationalUnitId) {
        return userDao.getAllUsersInOrganizationalUnit(organizationalUnitId);
    }

    @Override
    public UserDetails createUser(UserDetailsRepresentation userDetailsRepresentation) {
        if (getUserByUsername(userDetailsRepresentation.getUsername()) != null) {
            throw new AlreadyExistsException("user.alreadyExists");
        }

        OrganizationalUnit orgUnit = orgUnitDao.getOrgUnitById(userDetailsRepresentation.getUnitId());

        if (orgUnit == null) {
            throw new NotFoundException("org.unit.notFound");
        }

        UserDetails user = new UserDetails();

        if (userDetailsRepresentation.getUsername() != null) {
            user.setUsername(userDetailsRepresentation.getUsername());
        }
        if (userDetailsRepresentation.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userDetailsRepresentation.getPassword());
            user.setPassword(encodedPassword);
        }
        if (userDetailsRepresentation.getDirectPermissions() != null) {
            if (user.getPermissions() == null) {
                user.setPermissions(new HashSet<Permission>());
            } else {
                user.getPermissions().clear();
            }
            for (String permissionName : userDetailsRepresentation.getDirectPermissions()) {
                Permission permission = Permission.create(permissionName);
                user.getPermissions().add(permission);
            }
        }
        if (userDetailsRepresentation.getRoles() != null) {
            if (user.getUserRoles() == null) {
                user.setUserRoles(new ArrayList<UserRole>());
            } else {
                user.getUserRoles().clear();
            }
            for (String roleName : userDetailsRepresentation.getRoles()) {
                UserRole role = userRoleService.getRoleByName(roleName);
                if (role == null) {
                    throw new UserRoleNotFoundException("role.notFound", roleName);
                }
                user.getUserRoles().add(role);
            }
        }

        user.setOrganizationalUnit(orgUnit);
        userDao.save(user);
        return user;
    }

    @Override
    public void changePassword(Long userId, String newPassword, String oldPassword) {
        UserDetails userDetails = getUserById(userId);

        if (passwordEncoder.matches(oldPassword, userDetails.getPassword()) == false) {
            throw new InvalidStateException("Old password check failed");
        }

        String newEncodedPassword = passwordEncoder.encode(newPassword);
        userDetails.setPassword(newEncodedPassword);
        saveUser(userDetails);
    }

    @Override
    public void updateRoles(Long userId, List<String> roles) {
        UserDetails userDetails = getUserById(userId);
        if (userDetails.getUserRoles() == null) {
            userDetails.setUserRoles(new ArrayList<UserRole>());
        } else {
            userDetails.getUserRoles().clear();
        }
        for (String roleName : roles) {
            UserRole role = userRoleService.getRoleByName(roleName);
            if (role == null) {
                throw new UserRoleNotFoundException("role.notFound", roleName);
            }
            userDetails.getUserRoles().add(role);
        }
        saveUser(userDetails);
    }

    @Override
    public void updatePermissions(Long userId, List<String> permissions) {
        UserDetails userDetails = getUserById(userId);
        if (userDetails.getPermissions() == null) {
            userDetails.setPermissions(new HashSet<Permission>());
        } else {
            userDetails.getPermissions().clear();
        }
        for (String permissionName : permissions) {
            Permission permission = Permission.create(permissionName);
            userDetails.getPermissions().add(permission);
        }
        saveUser(userDetails);
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
