package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static org.hamcrest.CoreMatchers.notNullValue
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#createSubFolder(java.lang.String, java.lang.Long, cz.cvut.fel.integracniportal.representation.NameRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
public class FolderController_createSubFolder_Test extends AbstractIntegrationTestCase {

    @Test
    void "should create and return new sub folder"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/cesnet/folder/1001/folder", json)
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.id', notNullValue()))
                .andExpect(jsonPath('$.name').value("new folder"))
                .andExpect(jsonPath('$.breadcrumbs', hasSize(1)))
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
