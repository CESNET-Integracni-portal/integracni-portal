package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.command.node.MoveFileCommand
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#moveFolder(java.lang.String, java.lang.Long, cz.cvut.fel.integracniportal.representation.FolderParentRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
public class FileController_moveFile_Test extends AbstractIntegrationTestCase {

    @Autowired
    FileMetadataDao dao

    @Test
    void "should move file to different folder"() {
        createFolder("1", "src", null)
        createFolder("2", "dst", null)
        createFile("3", "foo", "1")

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("3"),
                FolderId.of("2"),
        ));

        assert dao.getByUUID("3").getParent().id == "2"
    }

    @Test(expected = DuplicateNameException)
    void "should throw exception on folder move resulting in duplicate folder names in a parent folder"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")
        createFile("3", "foo", null)

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("3"),
                FolderId.of("1"),
        ));
    }

    @Test
    void "moving file to the same folder does nothing"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("2"),
                FolderId.of("1"),
        ));

        assert dao.getByUUID("2").getParent().getId() == "1"
    }

    @Test
    void "should return 400 when trying to move folder to itself"() {
        def json = '{"parentId" : "1001"}'

        apiPost("space/cesnet/folder/1001/parentChange", json)
                .andExpect(status().isBadRequest())

    }

    @Test
    void "should return 404 error for non-existing folder"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/cesnet/folder/666/parentChange", json)
                .andExpect(status().isNotFound())
    }

    @Test
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/xxx/folder/1001/parentChange", json)
                .andExpect(status().isNotFound())
    }

}
