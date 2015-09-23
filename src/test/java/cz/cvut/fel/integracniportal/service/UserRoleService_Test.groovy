package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.domain.Permission
import cz.cvut.fel.integracniportal.exceptions.NotFoundException
import cz.cvut.fel.integracniportal.model.UserRole
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.junit.Assert.*

/**
 * @author Petr Strnad
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("users.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserRoleService_Test extends AbstractIntegrationTestCase {

    @Autowired
    private UserRoleService userRoleService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Authentication authentication;

    @Mock
    private User usr;

    @Test
    void "should return user role with id 101"() {
        UserRole userRole = userRoleService.getRoleById(101);
        assertNotNull(userRole);
        assertEquals(101, userRole.getId());
        assertEquals("externists", userRole.getName());
    }

    @Test
    void "should return user role with name 'externists'"() {
        UserRole userRole = userRoleService.getRoleByName("externists");
        assertNotNull(userRole);
        assertEquals(101, userRole.getId());
        assertEquals("externists", userRole.getName());
    }

    @Test
    void "should throw NotFoundException for nonexistent user role with id 1000"() {
        try {
            userRoleService.getRoleById(1000);
            fail("User role with id 1000 should not exist");
        } catch (NotFoundException e) {
            // OK
        }
    }

    @Test
    void "should return null for nonexistent user role with name 'xyz'"() {
        UserRole userRole = userRoleService.getRoleByName("xyz");
        assertNull(userRole);
    }

    @Test
    void "should return list of all user roles"() {
        List<UserRole> userRoleList = userRoleService.getAllRoles();
        assertEquals(2, userRoleList.size());
        assertEquals(101, userRoleList.get(0).getId());
        assertEquals("externists", userRoleList.get(0).getName());
        assertEquals(1, userRoleList.get(0).getPermissions().size());
        Permission permission = (Permission) userRoleList.get(0).getPermissions().toArray()[0];
        assertEquals("externists", permission.toString());
    }

    @Test
    void "should create a new role with permissions 'password' and 'externists'"() {
        UserRole userRole = new UserRole();
        userRole.setName("test");
        userRole.setDescription("Test role.");
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.CHANGE_PASSWORD);
        permissions.add(Permission.EDIT_EXTERNISTS);
        userRole.setPermissions(permissions);
        userRoleService.createRole(userRole);

        // Check that the role was created
        UserRole retrievedRole = userRoleService.getRoleByName("test");
        assertNotNull(retrievedRole);
        assertEquals("test", retrievedRole.getName());
        assertEquals("Test role.", retrievedRole.getDescription());
        assertEquals(2, retrievedRole.getPermissions().size());
        assertTrue(retrievedRole.getPermissions().contains(Permission.EDIT_EXTERNISTS));
        assertTrue(retrievedRole.getPermissions().contains(Permission.CHANGE_PASSWORD));
    }

    @Test
    void "should update user role 'externists'"() {
        // Check that the user role exists prior to testing
        UserRole userRole = userRoleService.getRoleByName("externists");
        assertNotNull(userRole);
        assertEquals(101, userRole.getId());
        assertEquals("externists", userRole.getName());
        assertEquals("Can edit externists.", userRole.getDescription());

        // Update the role
        userRole.setName("changedRole");
        userRole.setDescription("New description.");
        userRole.getPermissions().remove(Permission.EDIT_EXTERNISTS);
        userRole.getPermissions().add(Permission.CHANGE_PASSWORD);
        userRoleService.saveRole(userRole);

        // Check that the role is no longer available under the old name
        UserRole userRoleOld = userRoleService.getRoleByName("externists");
        assertNull(userRoleOld);

        // Check that the role has been changed
        UserRole userRoleUpdated = userRoleService.getRoleByName("changedRole");
        assertEquals(101, userRoleUpdated.getId());
        assertEquals("changedRole", userRoleUpdated.getName());
        assertEquals("New description.", userRoleUpdated.getDescription());
        assertEquals(1, userRoleUpdated.getPermissions().size());
        assertTrue(userRoleUpdated.getPermissions().contains(Permission.CHANGE_PASSWORD));
    }

    @Test
    void "should remove user role 'foo'"() {
        // Check that the user role exists prior to testing
        UserRole userRole = userRoleService.getRoleByName("foo");
        assertNotNull(userRole);
        assertEquals(102, userRole.getId());
        assertEquals("foo", userRole.getName());

        // Remove the user role
        userRoleService.deleteRole(userRole);

        // Check that the user role no longer exists
        try {
            userRoleService.getRoleById(102);
            fail("User role 'foo' with id 102 still exists after being removed.");
        } catch (NotFoundException e) {
            // OK
        }
        assertNull(userRoleService.getRoleByName("foo"));
    }

}
