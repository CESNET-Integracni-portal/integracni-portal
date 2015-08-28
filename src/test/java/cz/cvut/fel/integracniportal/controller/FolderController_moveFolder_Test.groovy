package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.command.node.MoveFolderCommand
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.junit.Ignore
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
@DatabaseSetup("classpath:user.xml")
public class FolderController_moveFolder_Test extends AbstractIntegrationTestCase {

    @Autowired
    FolderDao folderDao

    @Test
    void "should move folder to different folder"() {
        createFolder("1", "src", null)
        createFolder("2", "fooBar", "1")

        createFolder("3", "dst", null)

        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of("2"),
                FolderId.of("3")
        ))

        assert folderDao.get("2").getParent().getId() == "3"

    }

    @Test
    void "should move folder to root"() {
        createFolder("1", "dir", null)
        createFolder("2", "foo", "1")

        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of("2"),
                null,
        ));

        assert folderDao.get("2").getParent() == null
    }

    @Test
    void "should move folder from root to subfolder"() {
        createFolder("1", "dir", null)
        createFolder("2", "foo", null)

        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of("2"),
                FolderId.of("1"),
        ));

        assert folderDao.get("2").getParent().getId() == "1"
    }

    @Test(expected = DuplicateNameException)
    void "should throw exception on folder move resulting in duplicate folder names in the parent folder which is the root folder"() {
        createFolder("1", "src", null)
        createFolder("2", "fooBar", "1")

        createFolder("3", "fooBar", null)

        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of("2"),
                null
        ))
    }

    @Test
    void "should return 400 when trying to move folder to itself"() {
        createFolder("1", "root", null)
        createFolder("2", "dir", "1")

        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of("1"),
                FolderId.of("1"),
        ));
    }

    @Test
    void "moving folder to the same parent folder does nothing"() {
        createFolder("1", "root", null)
        createFolder("2", "dir", "1")

        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of("2"),
                FolderId.of("1"),
        ));

        assert folderDao.get("2").getParent().getId() == "1"
    }

    @Test
    void "moving folder to the same parent folder which is the root folder does nothing"() {
        createFolder("1", "root", null)

        commandGateway.sendAndWait(new MoveFolderCommand(
                FolderId.of("1"),
                null,
        ));

        assert folderDao.get("1").getParent() == null
    }

    @Test
    @Ignore
    void "should return 404 error for non-existing folder"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/cesnet/folder/666/parentChange", json)
                .andExpect(status().isNotFound())
    }

    @Test
    @Ignore
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/xxx/folder/1001/parentChange", json)
                .andExpect(status().isNotFound())
    }

}
