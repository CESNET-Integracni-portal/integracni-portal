package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.representation.LabelIdRepresentation
import cz.cvut.fel.integracniportal.representation.LabelRepresentation
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation
import cz.cvut.fel.integracniportal.service.FolderService
import cz.cvut.fel.integracniportal.service.LabelService
import cz.cvut.fel.integracniportal.service.UserDetailsService
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static junit.framework.Assert.assertEquals
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Eldar Iosip
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
public class AclController_Test extends AbstractIntegrationTestCase {

    @Test
    void "should return available node permissions with read at first"() {
        apiGet("acl/permission").andExpect(status().isOk())
                .andExpect(jsonPath('permissions.$[0]').value("READ"))
    }

    @Test
    void "should update acl with read at first"() {
        def json = getResourceAsString("permissions.json");

        apiPost("acl/space/cesnet/file/1001/user/1", json)
                .andExpect(status().isNoContent())
    }
}