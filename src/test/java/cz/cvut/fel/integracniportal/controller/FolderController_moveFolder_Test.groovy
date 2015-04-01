package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#moveFolder(java.lang.String, java.lang.Long, cz.cvut.fel.integracniportal.representation.FolderParentRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
public class FolderController_moveFolder_Test extends AbstractIntegrationTestCase {

    @Test
    void "should move folder to different folder"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/cesnet/folder/1001/parentChange", json)
                .andExpect(status().isNoContent())

        apiGet("space/cesnet/folder/1001")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.breadcrumbs[0].id').value("1002"))

    }

    @Test
    void "should return 400 when trying to move folder to itself"() {
        def json = '{"parentId" : "1001"}'

        apiPost("space/cesnet/folder/1001/parentChange", json)
                .andExpect(status().isBadRequest())

    }

    @Test
    void "should return 404 error for non-existing folder"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/cesnet/folder/666/parentChange", json)
                .andExpect(status().isNotFound())
    }

    @Test
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("parentFolder.json");

        apiPost("space/xxx/folder/1001/parentChange", json)
                .andExpect(status().isNotFound())
    }

}
