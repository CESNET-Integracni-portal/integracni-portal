package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static org.hamcrest.CoreMatchers.notNullValue
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link SpaceController#createFolder(java.lang.String, cz.cvut.fel.integracniportal.representation.NameRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
@Ignore
public class SpaceController_createFolder_Test extends AbstractIntegrationTestCase {

    @Test
    void "should create and return new folder in space root"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/cesnet/folder", json)
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id', notNullValue()))
                .andExpect(jsonPath('$.name').value("new folder"))
                .andExpect(jsonPath('$.breadcrumbs', hasSize(0)))
    }

    @Test
    void "should return 404 error for non-existing space"() {
        def json = getResourceAsString("folder.json");

        apiPost("space/xxx/folder", json)
                .andExpect(status().isNotFound())
    }

}
