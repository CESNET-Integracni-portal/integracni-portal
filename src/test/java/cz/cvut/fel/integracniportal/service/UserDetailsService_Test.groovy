package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.model.UserDetails
import org.junit.Before
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.junit.Assert.*
import static org.mockito.Mockito.when

/**
 * @author Petr Strnad
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("users.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserDetailsService_Test extends AbstractIntegrationTestCase {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Authentication authentication;

    @Mock
    private User usr;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userDetailsService.setAuthenticationService(authenticationService);
    }

    @Test
    void "should return user with id 101"() {
        UserDetails userDetails = userDetailsService.getUserById("101");
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), "101");
        assertEquals(userDetails.getUsername(), "a");
    }

    @Test
    void "should return user with username 'a'"() {
        UserDetails userDetails = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), "101");
        assertEquals(userDetails.getUsername(), "a");
    }

    @Test
    void "should return null for nonexistent user with username 'xyz'"() {
        UserDetails userDetails = userDetailsService.getUserByUsername("xyz");
        assertNull(userDetails);
    }

    @Test
    void "should return currently logged in user 'a'"() {
        when(usr.getUsername()).thenReturn("a");
        when(authentication.getPrincipal()).thenReturn(usr);
        when(authenticationService.getCurrentAuthentication()).thenReturn(authentication);

        UserDetails userDetails = userDetailsService.getCurrentUser();
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), "101");
        assertEquals(userDetails.getUsername(), "a");
    }

    @Test
    void "should return list of all users"() {
        List<UserDetails> userDetailsList = userDetailsService.getAllUsers();
        assertEquals(userDetailsList.size(), 2);
        assertEquals(userDetailsList.get(0).getId(), "101");
        assertEquals(userDetailsList.get(0).getUsername(), "a");
        assertEquals(userDetailsList.get(1).getId(), "102");
        assertEquals(userDetailsList.get(1).getUsername(), "b");
    }

    @Test
    void "should return list of users in organizational unit 1"() {
        List<UserDetails> usersInUnit = userDetailsService.getAllUsersInOrganizationalUnit("1");
        assertEquals(usersInUnit.size(), 1);
        assertEquals(usersInUnit.get(0).getId(), "101");
        assertEquals(usersInUnit.get(0).getUsername(), "a");
    }

}
