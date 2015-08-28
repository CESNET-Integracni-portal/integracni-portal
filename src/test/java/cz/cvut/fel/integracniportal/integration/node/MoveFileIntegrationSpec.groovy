package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.node.MoveFileCommand
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for {@link MoveFileCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class MoveFileIntegrationSpec extends AbstractIntegrationSpecification {

    @Autowired
    FileMetadataDao dao

    def "should move file to different folder"() {
        given:
            createFolder("1", "src", null)
            createFolder("2", "dst", null)
            createFile("3", "foo", "1")

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("3"),
                    FolderId.of("2"),
            ));

        then:
            dao.getByUUID("3").getParent().id == "2"
    }

    def "should move file to root"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("2"),
                    null,
            ));

        then:
            dao.getByUUID("2").getParent() == null
    }

    def "should move file from root to subfolder"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", null)

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("2"),
                    FolderId.of("1"),
            ));

        then:
            dao.getByUUID("2").getParent().getId() == "1"
    }

    def "moving file to the same folder does nothing"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("2"),
                    FolderId.of("1"),
            ));

        then:
            dao.getByUUID("2").getParent().getId() == "1"
    }

    def "moving file to the same folder which is the root does nothing"() {
        given:
            createFile("2", "foo", null)

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("2"),
                    null,
            ));

        then:
            dao.getByUUID("2").getParent() == null
    }

    def "should move file to the root folder"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("2"),
                    null,
            ));

        then:
            dao.getByUUID("2").getParent() == null
    }

    def "should throw exception on file move resulting in duplicate file names in the parent folder"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")
            createFile("3", "foo", null)

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("3"),
                    FolderId.of("1"),
            ));

        then:
            thrown(DuplicateNameException)
    }

    def "should throw exception on file move resulting in duplicate file names in the parent folder which is the root folder"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")
            createFile("3", "foo", null)

        when:
            commandGateway.sendAndWait(new MoveFileCommand(
                    FileId.of("2"),
                    null,
            ));

        then:
            thrown(DuplicateNameException)
    }

}
