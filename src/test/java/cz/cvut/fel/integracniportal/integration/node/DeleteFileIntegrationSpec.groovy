package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.node.DeleteFileCommand
import cz.cvut.fel.integracniportal.command.node.MoveFileCommand
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder

/**
 * Integration test for {@link MoveFileCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class DeleteFileIntegrationSpec extends AbstractIntegrationSpecification {

    def "should delete file in root folder"() {
        given:
            createFile("1", "foo", null)
            assert get(FileMetadata, "1") != null

        when:
            dispatch new DeleteFileCommand(FileId.of("1"))

        then:
            get(FileMetadata, "1") == null
    }

    def "should delete file in a folder"() {
        given:
            createFolder("1", "foo", null)
            createFile("2", "bar", "1")

            assert get(FileMetadata, "2") != null
            assert get(Folder, "1").files.size() == 1

        when:
            dispatch new DeleteFileCommand(FileId.of("2"))

        then:
            get(FileMetadata, "2") == null
            get(Folder, "1").childNodes.isEmpty()
    }

}
