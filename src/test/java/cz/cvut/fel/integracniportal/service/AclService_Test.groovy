package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.model.AccessControlPermission
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder
import cz.cvut.fel.integracniportal.model.Node
import org.apache.commons.io.IOUtils
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
public class AclService_Test extends AbstractIntegrationTestCase {

    final OWNER_ID = 80L
    final OWNER_USERNAME = "owner"
    final READER_USERNAME = "reader"

    @Autowired
    private AclService aclService;

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
    void "should add read permission for user 81 in owners root"() {
        Set<AccessControlPermission> permissions = new HashSet<AccessControlPermission>();
        permissions.add(AccessControlPermission.READ);

        aclService.updateNodeAcPermissionsByUser(71L, 81L, permissions);

        FileMetadata affectedFile = fileMetadataService.getFileMetadataById(71L)

        assertEquals(OWNER_ID, affectedFile.getOwner().getId())
        assertNull(affectedFile.getAcParent())
        assertNull(affectedFile.getParent())
        assertNull(affectedFile.getRootParent())

        //Check permission
        Set<AccessControlPermission> userPermissions = aclService.getAccessControlPermissions(71L, 81L);
        assertEquals(1, userPermissions.size())
        assertEquals(AccessControlPermission.READ, userPermissions.first())

        //Login as reader
        when(user.getUsername()).thenReturn(READER_USERNAME)
        when(authentication.getPrincipal()).thenReturn(user)
        when(authenticationService.getCurrentAuthentication()).thenReturn(authentication)

        //Get shared nodes
        Set<Node> nodes = aclService.getSharedNodes("cesnet", userDetailsService.getCurrentUser());
        assertNotNull(nodes)
        assertEquals(1, nodes.size())
        assertEquals(0, nodes.first().folders.size())
        assertEquals(0, nodes.first().files.size())
        assertEquals(71L, nodes.first().getId())
    }

    @Test
    void "should update permission for user 81 from download to upload"() {
        Set<AccessControlPermission> permissions = new HashSet<AccessControlPermission>();
        //Firstly add Download Permission
        permissions.add(AccessControlPermission.DOWNLOAD);
        aclService.updateNodeAcPermissionsByUser(74L, 81L, permissions);


        Folder affectedFolder = folderService.getFolderById(74L)

        assertEquals(OWNER_ID, affectedFolder.getOwner().getId())
        assertNull(affectedFolder.getAcParent())
        assertNull(affectedFolder.getParent())
        assertNull(affectedFolder.getRootParent())

        //Check permission
        Set<AccessControlPermission> userPermissions = aclService.getAccessControlPermissions(74L, 81L);
        assertEquals(1, userPermissions.size())
        assertEquals(AccessControlPermission.DOWNLOAD, userPermissions.first())

        //Update to upload, should override prev
        permissions.clear();
        permissions.add(AccessControlPermission.UPLOAD);

        aclService.updateNodeAcPermissionsByUser(74L, 81L, permissions);

        affectedFolder = folderService.getFolderById(74L)

        assertEquals(OWNER_ID, affectedFolder.getOwner().getId())
        assertNull(affectedFolder.getAcParent())
        assertNull(affectedFolder.getParent())
        assertNull(affectedFolder.getRootParent())

        //Check permission
        userPermissions = aclService.getAccessControlPermissions(74L, 81L);
        assertEquals(1, userPermissions.size())
        assertEquals(AccessControlPermission.UPLOAD, userPermissions.first())
    }

    @Test
    void "should make node with id 76 subroot of node id 73"() {
        Set<AccessControlPermission> permissions = new HashSet<AccessControlPermission>();
        //Firstly add Download Permission
        permissions.add(AccessControlPermission.READ);
        aclService.updateNodeAcPermissionsByUser(76L, 81L, permissions);

        Folder affectedFolder = folderService.getFolderById(76L)

        assertEquals(OWNER_ID, affectedFolder.getOwner().getId())
        assertNull(affectedFolder.getAcParent())
        assertEquals(73L, affectedFolder.getParent().getId())
        assertEquals(73L, affectedFolder.getRootParent().getId())
        assertEquals(1, affectedFolder.getAcEntries().size())
        assertTrue(affectedFolder.getAcEntries().get(0).getAccessControlPermissions().contains(AccessControlPermission.READ))

        //Check parent for subnode
        Folder parentFolder = folderService.getFolderById(73L)

        assertEquals(OWNER_ID, parentFolder.getOwner().getId())
        assertNull(parentFolder.getAcParent())
        assertNull(parentFolder.getParent())
        assertNull(parentFolder.getRootParent())

        println parentFolder.getAcSubnodes()
        assertEquals(2, parentFolder.getSubnodes().size())

        //assertEquals(1, parentFolder.getAcSubnodes().size()) Unable to test, lazy load?

        //Check children of new subroot
        FileMetadata file = fileMetadataService.getFileMetadataById(77L)

        assertEquals(OWNER_ID, file.getOwner().getId())
        assertEquals(76L, file.getParent().getId())
        assertNull(file.getRootParent())
        assertEquals(0, file.getAcEntries().size())
    }
}
