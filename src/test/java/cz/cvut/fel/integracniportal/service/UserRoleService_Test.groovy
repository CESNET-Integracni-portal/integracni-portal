package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.domain.Permission
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
        UserRole userRole = userRoleService.getRoleById("101");
        assertNotNull(userRole);
        assertEquals("101", userRole.getId());
        assertEquals("externists", userRole.getName());
    }

    @Test
    void "should return user role with name 'externists'"() {
        UserRole userRole = userRoleService.getRoleByName("externists");
        assertNotNull(userRole);
        assertEquals("101", userRole.getId());
        assertEquals("externists", userRole.getName());
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
        assertEquals("101", userRoleList.get(0).getId());
        assertEquals("externists", userRoleList.get(0).getName());
        assertEquals(1, userRoleList.get(0).getPermissions().size());
        Permission permission = (Permission) userRoleList.get(0).getPermissions().toArray()[0];
        assertEquals("externists", permission.toString());
    }

}
