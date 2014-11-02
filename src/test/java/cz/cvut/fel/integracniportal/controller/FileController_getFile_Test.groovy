package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata
import cz.cvut.fel.integracniportal.cesnet.CesnetService
import cz.cvut.fel.integracniportal.exceptions.FileAccessException
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.kubek2k.springockito.annotations.ReplaceWithMock
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class FileController_getFile_Test extends AbstractIntegrationTestCase {

    @Autowired
    @ReplaceWithMock
    CesnetService cesnetService;

    @Test
    void "should return the content of the file"() {
        def returnBody = "<html></html>"

        when(cesnetService.getFileMetadata("2"))
                .thenReturn(new CesnetFileMetadata(filename: "2"))

        when(cesnetService.getFile("2"))
                .thenReturn(IOUtils.toInputStream(returnBody, "UTF-8"))

        apiGet("file/2")
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", 'attachment; filename=\"b.html\"'))
                .andExpect(content().contentType("text/html"))
                .andExpect(content().string(returnBody))
    }


    @Test
    void "should return 404 Not Found for non existing file"() {
        apiGet("file/666")
                .andExpect(status().isNotFound())
    }


    @Test
    void "should return 503 Service Unavailable if ServiceAccessException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new ServiceAccessException("Service unavailable"))

        apiGet("file/2")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 503 Service Unavailable if FileAccessException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new FileAccessException("Service unavailable"))

        apiGet("file/2")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 404 Not Found if FileNotFoundException thrown"() {
        when(cesnetService.getFileMetadata("2"))
                .thenThrow(new FileNotFoundException("Not found"))

        apiGet("file/2")
                .andExpect(status().isNotFound())
    }

}
