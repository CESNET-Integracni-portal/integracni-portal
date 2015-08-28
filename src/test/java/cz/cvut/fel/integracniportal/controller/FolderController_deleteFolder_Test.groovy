package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder
import cz.cvut.fel.integracniportal.service.FileMetadataService
import cz.cvut.fel.integracniportal.service.FolderService
import cz.cvut.fel.integracniportal.service.UserDetailsService
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

/**
 * Test for {@link FolderController#moveFolderToBin(java.lang.String, java.lang.Long)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Ignore
public class FolderController_deleteFolder_Test extends AbstractIntegrationTestCase {

    @Autowired
    FolderService folderService

    @Autowired
    FileMetadataService fileMetadataService

    @Autowired
    UserDetailsService userDetailsService

    @Test
    void "should delete nested folders and files"() {
        Folder folder1 = new Folder(name: "a", parent: null, space: 'cesnet')
        folderService.createFolder(folder1, userDetailsService.getCurrentUser())

        def folder2 = folderService.createSubFolder("a", folder1, userDetailsService.getCurrentUser())
        def folder3 = folderService.createSubFolder("a", folder2, userDetailsService.getCurrentUser())

        FileMetadata fileMetadata = new FileMetadata(parent: folder3, owner: userDetailsService.getCurrentUser(), filename: "asd", space: 'cesnet', filesize: 4, mimetype: "application/json")
        fileMetadataService.createFileMetadata(fileMetadata)

        folderService.removeFolder(folder1.getId())

        try {
            folderService.getFolderById(folder1.getId())
            fail();
        } catch (Exception e) {
            // pass
        }
        try {
            folderService.getFolderById(folder2.getId())
            fail();
        } catch (Exception e) {
            // pass
        }
        try {
            folderService.getFolderById(folder3.getId())
            fail();
        } catch (Exception e) {
            // pass
        }
        try {
            fileMetadataService.getFileMetadataByUuid(fileMetadata.getId())
            fail();
        } catch (Exception e) {
            // pass
        }
    }

}
