package cz.cvut.fel.integracniportal.service;

import org.springframework.security.core.Authentication;

/**
 * Extension of {@link org.springframework.security.core.userdetails.UserDetailsService} which adds the option of returning
 * {@link org.springframework.security.core.Authentication} for the currently logged user.
 */
public interface AuthenticationService extends org.springframework.security.core.userdetails.UserDetailsService {

    /**
     * Returns authentication of the currently logged user.
     *
     * @return authentication of the currently logged user
     */
    public Authentication getCurrentAuthentication();

}
