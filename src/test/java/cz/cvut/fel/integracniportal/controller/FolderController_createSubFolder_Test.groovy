package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.command.node.CreateFolderCommand
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.Assert
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static org.hamcrest.CoreMatchers.notNullValue
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#createSubFolder(java.lang.String, java.lang.String, cz.cvut.fel.integracniportal.representation.NameRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
public class FolderController_createSubFolder_Test extends AbstractIntegrationTestCase {

    @Autowired
    CommandGateway commandGateway

    @Autowired
    FolderDao dao

    @Test
    void "should create and return new sub folder"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/cesnet/folder/1001/folder", json)
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.id', notNullValue()))
                .andExpect(jsonPath('$.name').value("new folder"))
                .andExpect(jsonPath('$.breadcrumbs', hasSize(1)))
    }

    @Test(expected = DuplicateNameException)
    void "should not create a folder of duplicate name"() {
        commandGateway.sendAndWait(new CreateFolderCommand(
                FolderId.of("1"),
                "docs",
                FolderId.of("1001"),
                UserId.of(1),
                "cesnet"
        ));
        commandGateway.sendAndWait(new CreateFolderCommand(
                FolderId.of("2"),
                "docs",
                FolderId.of("1001"),
                UserId.of(1),
                "cesnet"
        ));
        Assert.fail("should throw error on duplicate folder name");
    }

    @Test
    void "should return 404 error for non-existing folder"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/cesnet/folder/666/folder", json)
                .andExpect(status().isNotFound())
    }

    @Test
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/xxx/folder/1001/folder", json)
                .andExpect(status().isNotFound())
    }

}
