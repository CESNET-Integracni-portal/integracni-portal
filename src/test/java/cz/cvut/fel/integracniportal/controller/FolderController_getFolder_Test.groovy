package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#getFolder(java.lang.String, java.lang.Long)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
@Ignore
public class FolderController_getFolder_Test extends AbstractIntegrationTestCase {

    @Test
    void "should return folder"() {
        apiGet("space/cesnet/folder/1001")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.name').value("root1"))
    }

    @Test
    void "should return 404 error for non-existing folder"() {
        apiGet("space/cesnet/folder/666")
                .andExpect(status().isNotFound())
    }

    @Test
    void "should return 404 error for non-existing space"() {
        apiGet("space/xxx/folder/1001")
                .andExpect(status().isNotFound())
    }

}
