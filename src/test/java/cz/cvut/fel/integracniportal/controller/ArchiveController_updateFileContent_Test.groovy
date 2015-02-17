package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cesnet.CesnetService
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.ReplaceWithMock
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration

import static org.mockito.Matchers.any
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("classpath:fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArchiveController_updateFileContent_Test extends AbstractIntegrationTestCase {

    @Autowired
    @ReplaceWithMock
    CesnetService cesnetService

    @Autowired
    FileMetadataDao metadataDao

    @Test
    @Ignore
    void "should update the file data"() {
        when(cesnetService.uploadFile(any(InputStream), "2"))

        def file = new MockMultipartFile("x.json", "x.json", "application/json", "{}".getBytes())

        mockMvc.perform(fileUpload(fromApi("archive/file/2/content")).file(file))
                .andExpect(status().isNoContent())

        def meta = metadataDao.getByUUID("2")
    }


    @Test
    @Ignore
    void "should return 404 Not Found for non existing file"() {
        def file = new MockMultipartFile("x.json", "x.json", "application/json", "{}".getBytes())

        mockMvc.perform(fileUpload(fromApi("archive/file/666/content")).file(file))
                .andExpect(status().isNotFound())
    }

}
