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

/**
 * Test for {@link FileController#moveFile(java.lang.String, java.lang.String, cz.cvut.fel.integracniportal.representation.FolderParentRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:user.xml")
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

    @Test
    void "should move file to root"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("2"),
                null,
        ));

        assert dao.getByUUID("2").getParent() == null
    }

    @Test
    void "should move file from root to subfolder"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", null)

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("2"),
                FolderId.of("1"),
        ));

        assert dao.getByUUID("2").getParent().getId() == "1"
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
    void "moving file to the same folder which is the root does nothing"() {
        createFile("2", "foo", null)

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("2"),
                null,
        ));

        assert dao.getByUUID("2").getParent() == null
    }

    @Test
    void "should move file to the root folder"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("2"),
                null,
        ));

        assert dao.getByUUID("2").getParent() == null
    }

    @Test(expected = DuplicateNameException)
    void "should throw exception on file move resulting in duplicate file names in the parent folder"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")
        createFile("3", "foo", null)

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("3"),
                FolderId.of("1"),
        ));
    }

    @Test(expected = DuplicateNameException)
    void "should throw exception on file move resulting in duplicate file names in the parent folder which is the root folder"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")
        createFile("3", "foo", null)

        commandGateway.sendAndWait(new MoveFileCommand(
                FileId.of("2"),
                null,
        ));
    }

}
