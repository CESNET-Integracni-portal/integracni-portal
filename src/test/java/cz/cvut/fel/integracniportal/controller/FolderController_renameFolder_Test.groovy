package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.command.node.RenameFolderCommand
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#renameFolder(java.lang.String, java.lang.String, cz.cvut.fel.integracniportal.representation.NameRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:user.xml")
public class FolderController_renameFolder_Test extends AbstractIntegrationTestCase {

    @Autowired
    FolderDao dao

    @Test
    void "should rename folder"() {
        createFolder("1", "foo", null)

        commandGateway.sendAndWait(new RenameFolderCommand(
                FolderId.of("1"),
                "foo"
        ))

        assert dao.get("1").getName() == "foo";
    }

    @Test(expected = DuplicateNameException)
    void "should throw an exception if rename results in duplicate name in root folder"() {
        createFolder("1", "foo", null)
        createFolder("2", "bar", null)

        commandGateway.sendAndWait(new RenameFolderCommand(
                FolderId.of("2"),
                "foo"
        ))
    }

    @Test(expected = DuplicateNameException)
    void "should throw an exception if rename results in duplicate name in subfolder"() {
        createFolder("1", "root", null)
        createFolder("2", "foo", "1")
        createFolder("3", "bar", "1")

        commandGateway.sendAndWait(new RenameFolderCommand(
                FolderId.of("3"),
                "foo"
        ))
    }

    @Test
    void "renaming a folder to the same name does nothing"() {
        createFolder("1", "root", null)
        createFolder("2", "foo", "1")

        commandGateway.sendAndWait(new RenameFolderCommand(
                FolderId.of("2"),
                "foo"
        ))
    }

    @Test
    @Ignore
    void "should return 404 error for non-existing folder"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/cesnet/folder/666/nameChange", json)
                .andExpect(status().isNotFound())
    }

    @Test
    @Ignore
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/xxx/folder/1001/nameChange", json)
                .andExpect(status().isNotFound())
    }

}
