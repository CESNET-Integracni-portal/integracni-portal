package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.node.MoveFolderCommand
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import cz.cvut.fel.integracniportal.exceptions.IllegalOperationException
import org.springframework.beans.factory.annotation.Autowired

/**
 * Integration test for {@link MoveFolderCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class MoveFolderIntegrationSpec extends AbstractIntegrationSpecification {

    @Autowired
    FolderDao folderDao

    def "should move folder to different folder"() {
        given:
            createFolder("1", "src", null)
            createFolder("2", "fooBar", "1")
            createFolder("3", "dst", null)

        when:
            dispatch new MoveFolderCommand(FolderId.of("2"), FolderId.of("3"))

        then:
             folderDao.get("2").getParent().getId() == "3"

    }

    def "should move folder to root"() {
        given:
            createFolder("1", "dir", null)
            createFolder("2", "foo", "1")

        when:
            dispatch new MoveFolderCommand(FolderId.of("2"), null)

        then:
             folderDao.get("2").getParent() == null
    }

    def "should move folder from root to subfolder"() {
        given:
            createFolder("1", "dir", null)
            createFolder("2", "foo", null)

        when:
            dispatch new MoveFolderCommand(FolderId.of("2"), FolderId.of("1"))

        then:
             folderDao.get("2").getParent().getId() == "1"
    }

    def "should thrown exception when trying to move folder to itself"() {
        given:
            createFolder("1", "root", null)
            createFolder("2", "dir", "1")

        when:
            dispatch new MoveFolderCommand(FolderId.of("1"), FolderId.of("1"))

        then:
            thrown(IllegalOperationException)
    }

    def "moving folder to the same parent folder does nothing"() {
        given:
            createFolder("1", "root", null)
            createFolder("2", "dir", "1")

        when:
            dispatch new MoveFolderCommand(FolderId.of("2"), FolderId.of("1"))

        then:
             folderDao.get("2").getParent().getId() == "1"
    }

    def "moving folder to the same parent folder which is the root folder does nothing"() {
        given:
            createFolder("1", "root", null)

        when:
            dispatch new MoveFolderCommand(FolderId.of("1"), null)

        then:
             folderDao.get("1").getParent() == null
    }

    def "should throw exception on folder move resulting in duplicate folder names in the parent folder which is the root folder"() {
        given:
            createFolder("1", "src", null)
            createFolder("2", "fooBar", "1")
            createFolder("3", "fooBar", null)

        when:
            dispatch new MoveFolderCommand(FolderId.of("2"), null)

        then:
            thrown(DuplicateNameException)
    }

}
