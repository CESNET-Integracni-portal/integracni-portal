package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.model.AccessControlPermission
import org.junit.Before
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.junit.Assert.*
import static org.mockito.Mockito.when

/**
 * @author Eldar Iosip
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("nodes.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class AclService_getAccessControlPermissions_Test extends AbstractIntegrationTestCase {

    final OWNER_USERNAME = "owner"

    @Autowired
    private AclService aclService

    @Autowired
    private UserDetailsService userDetailsService

    @Autowired
    private FileMetadataService fileMetadataService

    @Autowired
    private FolderService folderService

    @Mock
    private AuthenticationService authenticationService

    @Mock
    private Authentication authentication

    @Mock
    private User user

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this)
        userDetailsService.setAuthenticationService(authenticationService)

        when(user.getUsername()).thenReturn(OWNER_USERNAME)
        when(authentication.getPrincipal()).thenReturn(user)
        when(authenticationService.getCurrentAuthentication()).thenReturn(authentication)
    }

    @Test
    void "should allow group member allowed to read, read the file"() {
        Set<AccessControlPermission> permissions = new HashSet<AccessControlPermission>()
        permissions.add(AccessControlPermission.READ)

        aclService.updateNodeAceByGroup(79L, 22L, permissions)

        //Check permission if group member with ID 82 has a READ
        Set<AccessControlPermission> userPermissions = aclService.getAccessControlPermissions(79L, 82L)
        assertEquals(1, userPermissions.size())
        assertEquals(AccessControlPermission.READ, userPermissions.first())
    }

    @Test
    void "should allow group member allowed to read, read the file from root"() {
        Set<AccessControlPermission> permissions = new HashSet<AccessControlPermission>()
        permissions.add(AccessControlPermission.READ)

        aclService.updateNodeAceByGroup(71L, 22L, permissions)

        //Check permission if group member with ID 82 has a READ
        Set<AccessControlPermission> userPermissions = aclService.getAccessControlPermissions(71L, 82L)
        assertEquals(1, userPermissions.size())
        assertEquals(AccessControlPermission.READ, userPermissions.first())
    }

    @Test
    void "should allow group member allowed to download, download the file somewhere in children"() {
        Set<AccessControlPermission> permissions = new HashSet<AccessControlPermission>()
        permissions.add(AccessControlPermission.DOWNLOAD)

        aclService.updateNodeAceByGroup(73L, 22L, permissions)

        //Check permission if group member with ID 82 has a READ
        Set<AccessControlPermission> userPermissions = aclService.getAccessControlPermissions(79L, 82L)
        assertEquals(1, userPermissions.size())
        assertEquals(AccessControlPermission.DOWNLOAD, userPermissions.first())
    }

    @Test
    void "should disallow group to download, with exception of user 82"() {
        Set<AccessControlPermission> permissions = new HashSet<AccessControlPermission>()
        permissions.add(AccessControlPermission.READ)

        aclService.updateNodeAceByGroup(79L, 22L, permissions)

        permissions.clear()
        permissions.add(AccessControlPermission.DOWNLOAD)

        aclService.updateNodeAceByUser(79L, 82L, permissions)

        Set<AccessControlPermission> userPermissions = aclService.getAccessControlPermissions(79L, 82L)
        assertEquals(2, userPermissions.size())
        assertTrue(userPermissions.contains(AccessControlPermission.DOWNLOAD))
    }
}
