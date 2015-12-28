package cz.cvut.fel.integracniportal.service

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder
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
public class FolderService_Test extends AbstractIntegrationTestCase {

    final OWNER_USERNAME = "owner"

    final FOLDER_NAME = "TESTED_FOLDER"
    final SPACE_NAME = "cesnet"

    @Autowired
    private UserDetailsService userDetailsService

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
    void "should create folder in root"() {
        Folder createdFolder = folderService.createTopLevelFolder(
                FOLDER_NAME,
                SPACE_NAME,
                userDetailsService.getCurrentUser()
        )

        assertNotNull(createdFolder)
        assertEquals(FOLDER_NAME, createdFolder.getName())
        assertEquals(SPACE_NAME, createdFolder.getSpace())
        assertNull(createdFolder.getAcParent())
        assertNull(createdFolder.getParent())
        assertNull(createdFolder.getRootParent())
    }

    @Test
    void "should create folder inside folder from root"() {
        Folder createdFolder = folderService.createSubFolder(
                FOLDER_NAME,
                74L,
                userDetailsService.getCurrentUser()
        )

        assertNotNull(createdFolder)
        assertEquals(FOLDER_NAME, createdFolder.getName())
        assertEquals(SPACE_NAME, createdFolder.getSpace())
        assertEquals(74L, createdFolder.getAcParent().getId())
        assertEquals(74L, createdFolder.getParent().getId())
        assertNull(createdFolder.getRootParent())
    }

    @Test
    void "should create folder inside a subroot folder"() {
        Folder createdFolder = folderService.createSubFolder(
                FOLDER_NAME,
                79L,
                userDetailsService.getCurrentUser()
        )

        assertNotNull(createdFolder)
        assertEquals(FOLDER_NAME, createdFolder.getName())
        assertEquals(SPACE_NAME, createdFolder.getSpace())
        assertEquals(79L, createdFolder.getAcParent().getId())
        assertEquals(79L, createdFolder.getParent().getId())
        assertNull(createdFolder.getRootParent())
    }

    @Test
    void "should create folder in folder side a subroot folder"() {
        Folder createdFolder = folderService.createSubFolder(
                FOLDER_NAME,
                79L,
                userDetailsService.getCurrentUser()
        )

        assertNotNull(createdFolder)
        assertEquals(FOLDER_NAME, createdFolder.getName())
        assertEquals(SPACE_NAME, createdFolder.getSpace())
        assertEquals(79L, createdFolder.getAcParent().getId())
        assertEquals(79L, createdFolder.getParent().getId())
        assertNull(createdFolder.getRootParent())

        //Create another folder inside a new folder
        Folder createdFolderInner = folderService.createSubFolder(
                FOLDER_NAME,
                createdFolder.getId(),
                userDetailsService.getCurrentUser()
        )

        assertNotNull(createdFolderInner)
        assertEquals(FOLDER_NAME, createdFolderInner.getName())
        assertEquals(SPACE_NAME, createdFolderInner.getSpace())
        assertEquals(79L, createdFolderInner.getAcParent().getId())
        assertEquals(createdFolder.getId(), createdFolderInner.getParent().getId())
        assertNull(createdFolderInner.getRootParent())
    }

    @Test
    void "move folder from tree bottom to root"() {
        folderService.moveFolder(79L, null)

        Folder movedFolder = folderService.getFolderById(79L);

        assertNotNull(movedFolder)
        assertEquals("dirtestinnerinner1", movedFolder.getName())
        assertEquals(SPACE_NAME, movedFolder.getSpace())
        assertNull(movedFolder.getAcParent())
        assertNull(movedFolder.getParent())
        assertNull(movedFolder.getRootParent())
    }

    @Test
    void "move folder from root into subroot"() {
        folderService.moveFolder(74L, 79L)

        Folder movedFolder = folderService.getFolderById(74L);

        assertNotNull(movedFolder)
        assertEquals("dirtest2", movedFolder.getName())
        assertEquals(SPACE_NAME, movedFolder.getSpace())

        //Assert moved folder
        assertEquals(79L, movedFolder.getAcParent().getId())
        assertEquals(79L, movedFolder.getParent().getId())
        assertNull(movedFolder.getRootParent())

        //Assert file inside moved folder
        FileMetadata file = movedFolder.getFiles().first();
        assertEquals(78L, file.getId())
        assertEquals(74L, file.getParent().getId())
        assertEquals(79L, file.getAcParent().getId())
    }

    @Test
    void "move folder from inner lever of tree into subroot"() {
        folderService.moveFolder(76L, 79L)

        Folder movedFolder = folderService.getFolderById(76L);

        assertNotNull(movedFolder)
        assertEquals("dirtestinner2", movedFolder.getName())
        assertEquals(SPACE_NAME, movedFolder.getSpace())

        //Assert moved folder
        assertEquals(79L, movedFolder.getAcParent().getId())
        assertEquals(79L, movedFolder.getParent().getId())
        assertNull(movedFolder.getRootParent())

        //Assert file inside moved folder
        FileMetadata file = movedFolder.getFiles().first();
        assertEquals(77L, file.getId())
        assertEquals(76L, file.getParent().getId())
        assertEquals(79L, file.getAcParent().getId())
    }
}
