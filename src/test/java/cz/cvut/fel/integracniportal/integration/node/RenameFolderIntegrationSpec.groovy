package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.node.RenameFolderCommand
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for {@link RenameFolderCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class RenameFolderIntegrationSpec extends AbstractIntegrationSpecification {

    @Autowired
    FolderDao folderDao

    def "should rename folder"() {
        given:
            createFolder("1", "foo", null)

        when:
            dispatch new RenameFolderCommand(FolderId.of("1"), "foo")

        then:
            folderDao.get("1").getName() == "foo";
    }

    def "should throw an exception if rename results in duplicate name in root folder"() {
        given:
            createFolder("1", "foo", null)
            createFolder("2", "bar", null)

        when:
            dispatch new RenameFolderCommand(FolderId.of("2"), "foo")

        then:
            thrown(DuplicateNameException)
    }

    def "should throw an exception if rename results in duplicate name in subfolder"() {
        given:
            createFolder("1", "root", null)
            createFolder("2", "foo", "1")
            createFolder("3", "bar", "1")

        when:
            dispatch new RenameFolderCommand(FolderId.of("3"), "foo")

        then:
            thrown(DuplicateNameException)
    }

    def "renaming a folder to the same name does nothing"() {
        given:
            createFolder("1", "root", null)
            createFolder("2", "foo", "1")

        when:
            dispatch new RenameFolderCommand(FolderId.of("2"), "foo")

        then:
            noExceptionThrown()
    }

}
