package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for {@link cz.cvut.fel.integracniportal.command.node.CreateFileCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class CreateFileIntegrationSpec extends AbstractIntegrationSpecification {

    @Autowired
    FileMetadataDao dao

    def "should create a file in root"() {
        when:
            createFile("1", "foo", null, "1", "cesnet", "application/json", "{}")

        then:
            def file = dao.getByUUID("1")
            file.getName() == "foo"
            file.getFilesize() == 2
            file.getMimetype() == "application/json"
            file.getOwner().getId() == "1"
            file.getParent() == null
    }

    def "should create a file in subfolder"() {
        given:
            createFolder("1", "dir", null)

        when:
            createFile("2", "foo", "1")

        then:
            def file = dao.getByUUID("2")
            file.getName() == "foo"
            file.getFilesize() == 2
            file.getMimetype() == "application/json"
            file.getOwner().getId() == "1"
            file.getParent().id == "1"
    }

    def "should not create a duplicate file in a folder"() {
        given:
            createFolder("1", "dir", null)
            createFile("2", "foo", "1")

        when:
            createFile("3", "foo", "1")

        then:
            thrown(DuplicateNameException)
    }

    def "should not create a duplicate file in the root folder"() {
        given:
            createFile("1", "foo", null)

        when:
            createFile("2", "foo", null)

        then:
            thrown(DuplicateNameException)
    }

}
