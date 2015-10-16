package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.node.MoveFileCommand
import cz.cvut.fel.integracniportal.command.node.UpdateFileContentsCommand
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FileId
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.service.FileUpload
import org.apache.commons.io.IOUtils

/**
 * Integration test for {@link MoveFileCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class UpdateFileContentsIntegrationSpec extends AbstractIntegrationSpecification {

    def "should delete file in root folder"() {
        given:
            createFile("1", "foo", null)
            assert get(FileMetadata, "1").filesize == 2

        when:
            dispatch new UpdateFileContentsCommand(FileId.of("1"), new FileUpload(
                    "abc",
                    "application/json",
                    IOUtils.toInputStream('{"x": 1}')
            ))

        then:
            get(FileMetadata, "1").filesize == 8
    }

}
