package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.cesnet.CesnetFileMetadata
import cz.cvut.fel.integracniportal.cesnet.CesnetService
import cz.cvut.fel.integracniportal.cesnet.FileState
import cz.cvut.fel.integracniportal.dao.FileMetadataDao
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.kubek2k.springockito.annotations.ReplaceWithMock
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArchiveController_updateFile_Test extends AbstractIntegrationTestCase {

    @Autowired
    @ReplaceWithMock
    CesnetService cesnetService

    @Autowired
    FileMetadataDao metadataDao

    @Test
    void "should update the file's metadata"() {
        when(cesnetService.getFileMetadata("2"))
                .thenReturn(new CesnetFileMetadata(filename: "2", filesize: 100, state: FileState.REG))

        def body = IOUtils.toString(getClass().getResourceAsStream("putFileMetadata.json"))

        apiPut("archive/file/2", body)
                .andExpect(status().isNoContent())

        def meta = metadataDao.getFileMetadataByUuid("2")

        assertEquals "x.jpg", meta.filename
        assertEquals "image/jpeg", meta.mimetype
    }


    @Test
    void "should return 404 Not Found for non existing file"() {
        apiGet("archive/file/666")
                .andExpect(status().isNotFound())
    }

}
