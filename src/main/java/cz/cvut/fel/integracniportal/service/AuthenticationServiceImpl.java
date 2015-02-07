package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.Permission;
import cz.cvut.fel.integracniportal.model.UserDetails;
import cz.cvut.fel.integracniportal.model.UserRole;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Service used by Spring to get access to user credentials during authentication.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    private UserDetailsService userService;

    /**
     * Called by Spring during authentication in order to find a user by his username.
     *
     * @param username Username of the user who is currently attempting to log in.
     * @return User credentials if the user exists.
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional(readOnly = true)
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) {
        UserDetails userEntity = userService.getUserByUsername(username);
        if (userEntity == null) {
            logger.info("Login credentials for user " + username + " not found.");
            throw new UsernameNotFoundException("User not found");
        }

        // For security purposes, we should return new Spring User object, not an entity
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        for (Permission permission : userEntity.getPermissions()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.toString()));
        }
        for (UserRole userRole : userEntity.getUserRoles()) {
            for (Permission permission : userRole.getPermissions()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.toString()));
            }
        }
        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                grantedAuthorities
        );
    }

    /**
     * Returns authentication of the currently logged user.
     *
     * @return authentication of the currently logged user
     */
    @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
