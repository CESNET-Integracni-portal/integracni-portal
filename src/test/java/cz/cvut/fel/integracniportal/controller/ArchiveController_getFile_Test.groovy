package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata
import cz.cvut.fel.integracniportal.cesnet.CesnetService
import cz.cvut.fel.integracniportal.cesnet.FileState
import cz.cvut.fel.integracniportal.exceptions.FileAccessException
import cz.cvut.fel.integracniportal.exceptions.FileNotFoundException
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
@DatabaseSetup("classpath:fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArchiveController_getFile_Test extends AbstractIntegrationTestCase {

    @Autowired
    @ReplaceWithMock
    CesnetService cesnetService

    @Test
    void "should return the file metadata json"() {
        when(cesnetService.getFileMetadata("2"))
                .thenReturn(new CesnetFileMetadata(filename: "2", filesize: 100, state: FileState.REG))

        apiGet("archive/file/2")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.uuid').value("2"))
                .andExpect(jsonPath('$.filename').value("b.html"))
                .andExpect(jsonPath('$.filesize').value(100))
                .andExpect(jsonPath('$.state').value("REG"))
                .andExpect(jsonPath('$.mimetype').value("text/html"))
                .andExpect(jsonPath('$.createdOn').value("2014-01-01T00:00+0100"))
                .andExpect(jsonPath('$.changedOn').value("2014-01-01T00:00+0100"))
    }


    @Test
    void "should return 404 Not Found for non existing file"() {
        apiGet("archive/file/666")
                .andExpect(status().isNotFound())
    }


    @Test
    void "should return 503 Service Unavailable if ServiceAccessException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new ServiceAccessException("cesnet.service.unavailable"))

        apiGet("archive/file/2")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 503 Service Unavailable if FileAccessException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new FileAccessException("cesnet.service.unavailable"))

        apiGet("archive/file/2")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 404 Not Found if FileNotFoundException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new FileNotFoundException("Not found"))

        apiGet("archive/file/2")
                .andExpect(status().isNotFound())
    }

}
