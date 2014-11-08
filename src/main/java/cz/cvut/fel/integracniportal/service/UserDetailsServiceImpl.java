package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException;
import cz.cvut.fel.integracniportal.exceptions.NotFoundException;
import cz.cvut.fel.integracniportal.exceptions.UserRoleNotFoundException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link cz.cvut.fel.integracniportal.service.UserDetailsService}.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDetailsDao userDao;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails getUserById(long userId) throws NotFoundException {
        UserDetails userDetails = userDao.getUserById(userId);
        if (userDetails == null) {
            throw new NotFoundException("user.notFound", userId);
        }
        return userDetails;
    }

    @Override
    public UserDetails getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Override
    public UserDetails getCurrentUser() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    @Transactional
    public UserDetails createUser(UserDetailsRepresentation userDetailsResource) throws UserRoleNotFoundException, AlreadyExistsException {
        if (getUserByUsername(userDetailsResource.getUsername()) != null) {
            throw new AlreadyExistsException("user.alreadyExists");
        }

        UserDetails user = new UserDetails();
        if (userDetailsResource.getUserRoles() != null) {
            List<UserRole> userRoles = new ArrayList<UserRole>(userDetailsResource.getUserRoles().size());
            for (String userRoleName : userDetailsResource.getUserRoles()) {
                UserRole userRole = userRoleService.getRoleByName(userRoleName);
                if (userRole == null) {
                    throw new UserRoleNotFoundException("role.notFound", userRoleName);
                }
                userRoles.add(userRole);
            }
            user.setUserRoles(userRoles);
        }
        user.setUsername(userDetailsResource.getUsername());
        String encodedPassword = passwordEncoder.encode(userDetailsResource.getPassword());
        user.setPassword(encodedPassword);
        userDao.saveUser(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = {UserRoleNotFoundException.class, NotFoundException.class})
    public UserDetails updateUser(Long userId, UserDetailsRepresentation userDetailsResource) throws UserRoleNotFoundException, NotFoundException {
        UserDetails userDetails = getUserById(userId);
        if (userDetailsResource.getUsername() != null) {
            userDetails.setUsername(userDetailsResource.getUsername());
        }
        if (userDetailsResource.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(userDetailsResource.getPassword());
            userDetails.setPassword(encodedPassword);
        }
        if (userDetailsResource.getUserRoles() != null) {
            userDetails.getUserRoles().clear();
            for (String roleName: userDetailsResource.getUserRoles()) {
                UserRole role = userRoleService.getRoleByName(roleName);
                if (role == null) {
                    throw new UserRoleNotFoundException("role.notFound", roleName);
                }
                userDetails.getUserRoles().add(role);
            }
        }
        userDao.saveUser(userDetails);
        return userDetails;
    }

    @Override
    public void saveUser(UserDetails user) {
        userDao.saveUser(user);
    }

    @Override
    public void removeUser(UserDetails user) {
        userDao.removeUser(user);
    }
}
