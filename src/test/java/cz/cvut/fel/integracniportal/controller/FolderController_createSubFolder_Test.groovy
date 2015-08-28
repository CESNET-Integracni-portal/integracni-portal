package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.dao.FolderDao
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#createSubFolder(java.lang.String, java.lang.String, cz.cvut.fel.integracniportal.representation.NameRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:user.xml")
public class FolderController_createSubFolder_Test extends AbstractIntegrationTestCase {

    @Autowired
    CommandGateway commandGateway

    @Autowired
    FolderDao dao

    @Test
    void "should create and return new sub folder"() {
        createFolder("1", "root", null)
        createFolder("2", "child", "1")

        assert dao.get("2").getParent().getId() == "1"
    }

    @Test(expected = DuplicateNameException)
    void "should not create a folder of duplicate name"() {
        createFolder("1", "root", null)
        createFolder("2", "foo", "1")

        createFolder("3", "foo", "1")

        Assert.fail("should throw error on duplicate folder name");
    }

    @Test
    @Ignore
    void "should return 404 error for non-existing folder"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/cesnet/folder/666/folder", json)
                .andExpect(status().isNotFound())
    }

    @Test
    @Ignore
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/xxx/folder/1001/folder", json)
                .andExpect(status().isNotFound())
    }

}
