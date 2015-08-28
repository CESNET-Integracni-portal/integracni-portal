package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#moveFolder(java.lang.String, java.lang.Long, cz.cvut.fel.integracniportal.representation.FolderParentRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:user.xml")
public class FolderController_moveFolder_Test extends AbstractIntegrationTestCase {

    @Test
    @Ignore
    void "should return 404 error for non-existing folder"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/cesnet/folder/666/parentChange", json)
                .andExpect(status().isNotFound())
    }

    @Test
    @Ignore
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/xxx/folder/1001/parentChange", json)
                .andExpect(status().isNotFound())
    }

}
