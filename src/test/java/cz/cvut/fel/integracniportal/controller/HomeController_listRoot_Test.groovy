package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cmis.AlfrescoService
import org.apache.chemistry.opencmis.client.api.CmisObject
import org.apache.chemistry.opencmis.client.api.Document
import org.apache.chemistry.opencmis.client.api.Folder
import org.apache.chemistry.opencmis.client.api.ItemIterable
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId
import org.junit.Before
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static org.mockito.Mockito.doReturn
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
/**
 * @author Petr Strnad
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class HomeController_listRoot_Test extends AbstractHomeControllerTest {

    @Test
    void "should return list of all root folders and files"() {

        apiGet("home")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value("00000000-0000-0000-0000-000000000001"))
                .andExpect(jsonPath('$.name').value("home"))
                .andExpect(jsonPath('$.breadcrumbs').isArray())
                .andExpect(jsonPath('$.breadcrumbs', hasSize(0)))
                .andExpect(jsonPath('$.createdOn').value(949359600000L))
                .andExpect(jsonPath('$.changedOn').value(949446000000L))

                .andExpect(jsonPath('$.folders').isArray())
                .andExpect(jsonPath('$.folders', hasSize(1)))
                .andExpect(jsonPath('$.folders[0].id').value("00000000-0000-0000-0000-000000000002"))
                .andExpect(jsonPath('$.folders[0].name').value("subfolder"))
                .andExpect(jsonPath('$.folders[0].createdOn').value(949532400000L))
                .andExpect(jsonPath('$.folders[0].changedOn').value(949618800000L))

                .andExpect(jsonPath('$.files').isArray())
                .andExpect(jsonPath('$.files', hasSize(1)))
                .andExpect(jsonPath('$.files[0].uuid').value("00000000-0000-0000-0000-000000000003"))
                .andExpect(jsonPath('$.files[0].filename').value("document"))
                .andExpect(jsonPath('$.files[0].filesize').value(1000))
                .andExpect(jsonPath('$.files[0].mimetype').value("text/plain"))
                .andExpect(jsonPath('$.files[0].createdOn').value(949705200000L))
                .andExpect(jsonPath('$.files[0].changedOn').value(949791600000L));
    }

}
