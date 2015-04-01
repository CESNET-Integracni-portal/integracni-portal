package cz.cvut.fel.integracniportal.controller

import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link SpaceController#getSpaces()}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class SpaceController_getSpaces_Test extends AbstractIntegrationTestCase {

    @Test
    void "should return list of available spaces"() {
        apiGet("space")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].type').value("cesnet"))
                .andExpect(jsonPath('$[0].name').value("CESNET"))
    }

}
