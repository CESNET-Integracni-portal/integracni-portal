package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import com.jcraft.jsch.SftpException
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cesnet.CesnetService
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import org.junit.Test
import org.kubek2k.springockito.annotations.ReplaceWithMock
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.junit.Assert.fail
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArchiveController_deleteFile_Test extends AbstractIntegrationTestCase {

    @Autowired
    @ReplaceWithMock
    CesnetService cesnetService

    @Autowired
    FileMetadataDao metadataDao

    @Test
    void "should delete the file and its metadata"() {
        apiDelete("archive/file/2")
                .andExpect(status().isNoContent())

        try {
            metadataDao.getFileMetadataByUuid("2")
            fail("File metadata still exist")

        } catch (FileNotFoundException e) {
            // OK
        }

        verify(cesnetService).deleteFile("2")
    }


    @Test
    void "should return 503 Service Unavailable if FileAccessException thrown"() {
        when(cesnetService.deleteFile("2"))
                .thenThrow(new SftpException(0, ""))

        apiDelete("archive/file/2")
                .andExpect(status().isServiceUnavailable())
    }


    @Test
    void "should return 404 Not Found for non existing file"() {
        apiGet("archive/file/666")
                .andExpect(status().isNotFound())
    }

}
