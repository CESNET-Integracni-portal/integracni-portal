package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for {@link cz.cvut.fel.integracniportal.command.node.CreateFolderCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class CreateFolderIntegrationSpec extends AbstractIntegrationSpecification {

    @Autowired
    FolderDao dao

    def "should create a folder in root"() {
        when:
            createFolder("1", "foo", null)
            def folder = dao.get("1")

        then:
            folder.getName() == "foo"
            folder.getParent() == null
    }

    def "should create a folder in subfolder"() {
        given:
            createFolder("1", "dir", null)

        when:
            createFolder("2", "foo", "1")

        then:
            dao.get("2").getParent().id == "1"
    }

    def "should not create a duplicate folder in a folder"() {
        given:
            createFolder("1", "dir", null)
            createFolder("2", "foo", "1")

        when:
            createFolder("3", "foo", "1")

        then:
            thrown(DuplicateNameException)
    }

    def "should not create a duplicate folder in the root folder"() {
        given:
            createFolder("1", "foo", null)

        when:
            createFolder("2", "foo", null)

        then:
            thrown(DuplicateNameException)
    }

}
