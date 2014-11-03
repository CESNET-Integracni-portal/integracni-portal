package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.exceptions.UserRoleNotFoundException;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;
import cz.cvut.fel.integracniportal.resource.UserDetailsResource;
import org.springframework.beans.factory.annotation.Autowired;
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
    public UserDetails getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public UserDetails getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Override
    public List<UserDetails> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    @Transactional
    public UserDetails createUser(UserDetailsResource userDetailsResource) throws UserRoleNotFoundException {
        UserDetails user = new UserDetails();
        List<UserRole> userRoles = new ArrayList<UserRole>(userDetailsResource.getUserRoles().size());
        for (String userRoleName: userDetailsResource.getUserRoles()) {
            UserRole userRole = userRoleService.getRoleByName(userRoleName);
            if (userRole == null) {
                throw new UserRoleNotFoundException("Uživatelská role " + userRoleName + " neexistuje.");
            }
            userRoles.add(userRole);
        }
        user.setUserRoles(userRoles);
        user.setUsername(userDetailsResource.getUsername());
        String encodedPassword = passwordEncoder.encode(userDetailsResource.getPassword());
        user.setPassword(encodedPassword);
        userDao.saveUser(user);
        return user;
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
