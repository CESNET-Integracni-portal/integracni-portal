package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.exceptions.IllegalOperationException
import cz.cvut.fel.integracniportal.exceptions.NotFoundException
import cz.cvut.fel.integracniportal.model.UserDetails
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation
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
        UserDetails userDetails = userDetailsService.getUserById(101);
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), 101);
        assertEquals(userDetails.getUsername(), "a");
    }

    @Test
    void "should return user with username 'a'"() {
        UserDetails userDetails = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), 101);
        assertEquals(userDetails.getUsername(), "a");
    }

    @Test
    void "should throw NotFoundException for nonexistent user with id 1000"() {
        try {
            userDetailsService.getUserById(1000);
            fail("User with id 1000 should not exist");
        } catch (NotFoundException e) {
            // OK
        }
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
        assertEquals(userDetails.getId(), 101);
        assertEquals(userDetails.getUsername(), "a");
    }

    @Test
    void "should return list of all users"() {
        List<UserDetails> userDetailsList = userDetailsService.getAllUsers();
        assertEquals(userDetailsList.size(), 2);
        assertEquals(userDetailsList.get(0).getId(), 101);
        assertEquals(userDetailsList.get(0).getUsername(), "a");
        assertEquals(userDetailsList.get(1).getId(), 102);
        assertEquals(userDetailsList.get(1).getUsername(), "b");
    }

    @Test
    void "should return list of users in organizational unit 1"() {
        List<UserDetails> usersInUnit = userDetailsService.getAllUsersInOrganizationalUnit(1);
        assertEquals(usersInUnit.size(), 1);
        assertEquals(usersInUnit.get(0).getId(), 101);
        assertEquals(usersInUnit.get(0).getUsername(), "a");
    }

    @Test
    void "should create a new user"() {
        UserDetailsRepresentation userDetailsRepresentation = new UserDetailsRepresentation();
        userDetailsRepresentation.setUsername("newUser");
        userDetailsRepresentation.setPassword("password");
        userDetailsRepresentation.setUnitId("1");
        userDetailsService.createUser(userDetailsRepresentation);

        // Confirm that the user has been created
        UserDetails userDetails = userDetailsService.getUserByUsername("newUser");
        assertNotNull(userDetails);
        assertEquals(userDetails.getUsername(), "newUser");
        assertTrue(passwordEncoder.matches("password", userDetails.getPassword()));
        assertEquals(userDetails.getOrganizationalUnit().getId(), "1");

        // Confirm that the user has been added to a unit
        List<UserDetails> usersInUnit = userDetailsService.getAllUsersInOrganizationalUnit(1);
        boolean userFound = false;
        for (UserDetails userInUnit: usersInUnit) {
            if (userInUnit.getUsername().equals("newUser")) {
                userFound = true;
                break;
            }
        }
        assertTrue(userFound);
    }

    @Test
    void "should update user a's password"() {
        // Check that the user exists prior to testing
        UserDetails userDetails = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails);
        userDetails.setPassword(passwordEncoder.encode("myOldPassword"))
        userDetailsService.saveUser(userDetails)

        userDetailsService.changePassword(userDetails.getId(), "myNewPassword", "myOldPassword")

        UserDetails userDetailsUpdated = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetailsUpdated)
        assertTrue(passwordEncoder.matches("myNewPassword", userDetailsUpdated.getPassword()));
    }

    @Test
    void "should not update user a's password if old password doesn't match"() {
        // Check that the user exists prior to testing
        UserDetails userDetails = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails);
        userDetails.setPassword(passwordEncoder.encode("myOldPassword"))
        userDetailsService.saveUser(userDetails)

        try {
            userDetailsService.changePassword(userDetails.getId(), "myNewPassword", "wrongOldPassword")
            fail("should have thrown exception")
        } catch (IllegalOperationException e) {
            // ok
        }

        UserDetails userDetails2 = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails2)
        assertTrue(passwordEncoder.matches("myOldPassword", userDetails2.getPassword()));
    }

    @Test
    void "should update user a's roles"() {
        // Check that the user exists prior to testing
        UserDetails userDetails = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), 101);
        assertTrue(userDetails.getUserRoles().size() == 1);

        userDetailsService.updateRoles(userDetails.getId(), ["externists", "foo"]);

        UserDetails userDetailsUpdated = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetailsUpdated)
        assertTrue(userDetailsUpdated.getUserRoles().size() == 2);
    }

    @Test
    void "should update user a's permissions"() {
        // Check that the user exists prior to testing
        UserDetails userDetails = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), 101);
        assertTrue(userDetails.getPermissions().size() == 0);

        userDetailsService.updatePermissions(userDetails.getId(), ["externists", "password"]);

        UserDetails userDetailsUpdated = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetailsUpdated)
        assertTrue(userDetailsUpdated.getPermissions().size() == 2);
    }

    @Test
    void "should remove user 'a' with id 101"() {
        // Check that the user exists prior to testing
        UserDetails userDetails = userDetailsService.getUserByUsername("a");
        assertNotNull(userDetails);
        assertEquals(userDetails.getId(), 101);
        assertEquals(userDetails.getUsername(), "a");

        // Remove the user
        userDetailsService.removeUser(userDetails);

        // Check that the user no longer exists
        try {
            userDetailsService.getUserById(101);
            fail("User 'a' with id 101 still exists after being removed.");
        } catch (NotFoundException e) {
            // OK
        }
        assertNull(userDetailsService.getUserByUsername("a"));
    }

}
