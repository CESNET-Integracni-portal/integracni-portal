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
public class ArchiveController_getFolder_Test extends AbstractIntegrationTestCase {

    @Test
    void "should return the folder resource"() {
        apiGet("archive/folder/2")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value("2"))
                .andExpect(jsonPath('$.name').value("root2"))

                .andExpect(jsonPath('$.files').isArray())
                .andExpect(jsonPath('$.files', hasSize(1)))
                .andExpect(jsonPath('$.files[0].uuid').value("4"))

                .andExpect(jsonPath('$.folders').isArray())
                .andExpect(jsonPath('$.folders', hasSize(1)))
                .andExpect(jsonPath('$.folders[0].id').value("3"))
    }

}
