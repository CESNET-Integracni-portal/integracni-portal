package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArchiveController_listRootFolders_Test extends AbstractIntegrationTestCase {

    @Test
    void "should return list of all root folders"() {
        apiGet("archive")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$', hasSize(2)))
                .andExpect(jsonPath('$[0].id').value("1"))
                .andExpect(jsonPath('$[1].id').value("2"))
                .andExpect(jsonPath('$[0].name').value("root1"))
                .andExpect(jsonPath('$[1].name').value("root2"))
    }

}
