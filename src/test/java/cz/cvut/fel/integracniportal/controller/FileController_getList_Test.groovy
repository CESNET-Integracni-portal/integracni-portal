package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata
import cz.cvut.fel.integracniportal.cesnet.CesnetService
import cz.cvut.fel.integracniportal.exceptions.FileAccessException
import cz.cvut.fel.integracniportal.exceptions.ServiceAccessException
import org.junit.Test
import org.kubek2k.springockito.annotations.ReplaceWithMock
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class FileController_getList_Test extends AbstractIntegrationTestCase {

    @Autowired
    @ReplaceWithMock
    CesnetService cesnetService;

    @Test
    void "should return list of all files"() {
        when(cesnetService.getFileList()).thenReturn([
                new CesnetFileMetadata(filename: "1"),
                new CesnetFileMetadata(filename: "2")
        ])

        apiGet("files")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$').isArray())
                .andExpect(jsonPath('$', hasSize(2)))
                .andExpect(jsonPath('$[0].uuid').value("1"))
                .andExpect(jsonPath('$[1].uuid').value("2"))
                .andExpect(jsonPath('$[0].filename').value("a.txt"))
                .andExpect(jsonPath('$[1].filename').value("b.html"))
                .andExpect(jsonPath('$[0].mimetype').value("text/plain"))
                .andExpect(jsonPath('$[1].mimetype').value("text/html"))
    }


    @Test
    void "should return 503 Service Unavailable if ServiceAccessException thrown"() {
        when(cesnetService.getFileList())
                .thenThrow(new ServiceAccessException("Service unavailable"))

        apiGet("files")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 503 Service Unavailable if FileAccessException thrown"() {
        when(cesnetService.getFileList())
                .thenThrow(new FileAccessException("Service unavailable"))

        apiGet("files")
                .andExpect(status().isServiceUnavailable())
    }

}
