package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata
import cz.cvut.fel.integracniportal.cesnet.CesnetService
import cz.cvut.fel.integracniportal.cesnet.FileState
import cz.cvut.fel.integracniportal.exceptions.FileAccessException
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException
import org.junit.Test
import org.kubek2k.springockito.annotations.ReplaceWithMock
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class CesnetFileController_getFileMetadata_Test extends AbstractIntegrationTestCase {

    @Autowired
    @ReplaceWithMock
    CesnetService cesnetService;

    @Test
    void "should return the file metadata json"() {
        when(cesnetService.getFileMetadata("2"))
                .thenReturn(new CesnetFileMetadata(filename: "2", filesize: 100, state: FileState.REG))

        apiGet("archive/2/metadata")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.uuid').value("2"))
                .andExpect(jsonPath('$.filename').value("b.html"))
                .andExpect(jsonPath('$.filesize').value(100))
                .andExpect(jsonPath('$.state').value("REG"))
                .andExpect(jsonPath('$.mimetype').value("text/html"))
                .andExpect(jsonPath('$.createdOn').value("2013-12-31T23:00+0000"))
                .andExpect(jsonPath('$.changedOn').value("2013-12-31T23:00+0000"))
                .andExpect(jsonPath('$.archiveOn').value(null))
                .andExpect(jsonPath('$.deleteOn').value(null))
    }


    @Test
    void "should return 404 Not Found for non existing file"() {
        apiGet("archive/666/metadata")
                .andExpect(status().isNotFound())
    }


    @Test
    void "should return 503 Service Unavailable if ServiceAccessException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new ServiceAccessException("Service unavailable"))

        apiGet("archive/2/metadata")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 503 Service Unavailable if FileAccessException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new FileAccessException("Service unavailable"))

        apiGet("archive/2/metadata")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 404 Not Found if FileNotFoundException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new FileNotFoundException("Not found"))

        apiGet("archive/2/metadata")
                .andExpect(status().isNotFound())
    }

}
