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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link SpaceController#getRoot(java.lang.String, java.util.List)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
@Ignore
public class SpaceController_getRoot_Test extends AbstractIntegrationTestCase {

    @Test
    void "should return list of top level files and folders"() {
        apiGet("space/cesnet")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.folders').isArray())
                .andExpect(jsonPath('$.folders', hasSize(2)))
                .andExpect(jsonPath('$.files').isArray())
                .andExpect(jsonPath('$.files', hasSize(1)))
    }

    @Test
    void "should return 404 error for non-existing space"() {
        apiGet("space/xxx")
                .andExpect(status().isNotFound())
    }


}
