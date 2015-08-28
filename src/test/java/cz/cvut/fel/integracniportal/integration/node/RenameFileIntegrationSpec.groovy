package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.node.RenameFileCommand
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for {@link RenameFileCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class RenameFileIntegrationSpec extends AbstractIntegrationSpecification {

    @Autowired
    FileMetadataDao fileDao

    def "should rename a file"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")

        when:
            commandGateway.sendAndWait(new RenameFileCommand(
                    FileId.of("2"),
                    "fooBar"
            ));

        then:
            fileDao.getByUUID("2").getName() == "fooBar"
    }

    def "renaming a file to the same name does nothing"() {
        given:
            createFile("1", "foo", null)

        when:
            commandGateway.sendAndWait(new RenameFileCommand(
                    FileId.of("1"),
                    "foo"
            ))

        then:
            fileDao.getByUUID("1").getName() == "foo"
    }

    def "should throw exception on file rename resulting in duplicate file names in the parent folder"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")
            createFile("3", "bar", "1")

        when:
            commandGateway.sendAndWait(new RenameFileCommand(
                    FileId.of("3"),
                    "foo"
            ));

        then:
            thrown(DuplicateNameException)
    }

    def "should throw exception on file rename resulting in duplicate file names in the parent folder which is the root folder"() {
        given:
            createFile("1", "foo", null)
            createFile("2", "bar", null)

        when:
            commandGateway.sendAndWait(new RenameFileCommand(
                    FileId.of("2"),
                    "foo"
            ));

        then:
            thrown(DuplicateNameException)
    }

}
