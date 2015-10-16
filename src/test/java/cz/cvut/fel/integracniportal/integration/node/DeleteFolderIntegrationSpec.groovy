package cz.cvut.fel.integracniportal.integration.node

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.node.DeleteFolderCommand
import cz.cvut.fel.integracniportal.command.node.RenameFolderCommand
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.model.FileMetadata
import cz.cvut.fel.integracniportal.model.Folder

/**
 * Integration test for {@link RenameFolderCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class DeleteFolderIntegrationSpec extends AbstractIntegrationSpecification {

    def "should delete folder in root folder"() {
        given:
            createFolder("1", "foo", null)
            assert get(Folder, "1") != null

        when:
            dispatch new DeleteFolderCommand(FolderId.of("1"))

        then:
            get(Folder, "1") == null
    }

    def "should delete folder in a subfolder"() {
        given:
            createFolder("1", "foo", null)
            createFolder("2", "bar", "1")

            assert get(Folder, "2") != null
            assert get(Folder, "1").folders.size() == 1

        when:
            dispatch new DeleteFolderCommand(FolderId.of("2"))

        then:
            get(Folder, "2") == null
            get(Folder, "1").folders.isEmpty()
    }

    def "should delete folder containing files and folders"() {
        given:
            createFolder("top", "foo", null)
            createFolder("1", "foo", "top")

            createFolder("2", "bar", "1")
            createFile("3", "baz", "1")

            createFile("4", "baz2", "2")

            get(Folder, "top") != null

            def folder = get(Folder, "1")
            assert folder != null
            assert folder.getFiles().size() == 1
            assert folder.getFolders().size() == 1

            def folder2 = folder.getFolders().first()
            assert folder2.getFiles().size() == 1
            assert folder2.getFolders().isEmpty()

        when:
            dispatch new DeleteFolderCommand(FolderId.of("1"))

        then:
            get(Folder, "top").childNodes.isEmpty()

            get(Folder, "1") == null
            get(Folder, "2") == null
            get(FileMetadata, "3") == null
            get(FileMetadata, "4") == null
    }

}
