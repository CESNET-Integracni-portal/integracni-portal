package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder
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

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.mockito.Mockito.when

/**
 * @author Eldar Iosip
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("nodes.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class FileMetadataService_Test extends AbstractIntegrationTestCase {

    final FILE_NAME = "test.json"
    final MIME_TYPE = "application/json"

    final OWNER_ID = 80L
    final OWNER_USERNAME = "owner"

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
    void "should upload file to root"() {
        FileMetadata createdFile = fileMetadataService.uploadFileToRoot("cesnet", uploadFile())

        assertNotNull(createdFile)
        assertEquals(FILE_NAME, createdFile.getName())
        assertEquals(OWNER_ID, createdFile.getOwner().getId())
        assertNull(createdFile.getAcParent())
        assertNull(createdFile.getParent())
    }

    @Test
    void "should upload file into folder without permissions"() {
        FileMetadata createdFile = fileMetadataService.uploadFileToFolder(74L, uploadFile())

        assertNotNull(createdFile)
        assertEquals(FILE_NAME, createdFile.getName())
        assertEquals(OWNER_ID, createdFile.getOwner().getId())
        assertEquals(74L, createdFile.getAcParent().getId())
        assertEquals(74L, createdFile.getParent().getId())
    }

    @Test
    void "should move file into the same folder"() {
        fileMetadataService.moveFile(78L, 74L)

        FileMetadata movedFile = fileMetadataService.getFileMetadataById(78L)

        assertNotNull(movedFile)
        assertEquals(78L, movedFile.getId())
        assertEquals(OWNER_ID, movedFile.getOwner().getId())
        assertEquals(74L, movedFile.getAcParent().getId())
        assertEquals(74L, movedFile.getParent().getId())
    }

    @Test
    void "should add file to folder and inherit its acParent"() {
        FileMetadata createdFile = fileMetadataService.uploadFileToFolder(76L, uploadFile())

        assertNotNull(createdFile)
        assertEquals(OWNER_ID, createdFile.getOwner().getId())
        assertEquals(73L, createdFile.getAcParent().getId())
        assertEquals(76L, createdFile.getParent().getId())
    }

    @Test
    void "should add file to folder with modified acl"() {
        Folder parentFolder = folderService.getFolderById(79L)

        assertEquals(75L, parentFolder.getParent().getId())
        assertNull(parentFolder.getAcParent())

        FileMetadata createdFile = fileMetadataService.uploadFileToFolder(79L, uploadFile())

        assertNotNull(createdFile)
        assertEquals(OWNER_ID, createdFile.getOwner().getId())
        assertEquals(79L, createdFile.getAcParent().getId())
        assertEquals(79L, createdFile.getParent().getId())
    }

    def uploadFile() {
        return new FileUpload(FILE_NAME, MIME_TYPE, IOUtils.toInputStream("{}"))
    }
}
