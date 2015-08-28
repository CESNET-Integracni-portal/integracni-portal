package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.command.node.RenameFileCommand
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

/**
 * Test for {@link FileController#renameFile(java.lang.String, java.lang.String, cz.cvut.fel.integracniportal.representation.NameRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:user.xml")
public class FileController_renameFile_Test extends AbstractIntegrationTestCase {

    @Autowired
    FileMetadataDao fileDao

    @Test
    void "should rename a file"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")

        commandGateway.sendAndWait(new RenameFileCommand(
                FileId.of("2"),
                "fooBar",
        ));

        fileDao.getByUUID("2").getName() == "fooBar"
    }

    @Test
    void "renaming a file to the same name does nothing"() {
        createFile("1", "foo", null)

        commandGateway.sendAndWait(new RenameFileCommand(
                FileId.of("1"),
                "foo"
        ))

        assert fileDao.getByUUID("1").getName() == "foo"
    }

    @Test(expected = DuplicateNameException)
    void "should throw exception on file rename resulting in duplicate file names in the parent folder"() {
        createFolder("1", "dir", null)
        createFile("2", "foo", "1")
        createFile("3", "bar", "1")

        commandGateway.sendAndWait(new RenameFileCommand(
                FileId.of("3"),
                "foo",
        ));
    }

    @Test(expected = DuplicateNameException)
    void "should throw exception on file rename resulting in duplicate file names in the parent folder which is the root folder"() {
        createFile("1", "foo", null)
        createFile("2", "bar", null)

        commandGateway.sendAndWait(new RenameFileCommand(
                FileId.of("2"),
                "foo",
        ));
    }

}
