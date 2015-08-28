package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Ignore
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.hamcrest.CoreMatchers.notNullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#uploadFile(java.lang.String, java.lang.Long, org.springframework.web.multipart.MultipartFile)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
@Ignore
public class FolderController_uploadFile_Test extends AbstractIntegrationTestCase {

    final FILE_NAME = "x.json"
    final MIME_TYPE = "application/json"
    final FILE_CONTENTS = "{}";

    @Test
    void "should upload a file and return meta data"() {
        mockMvc.perform(fileUpload(fromApi("space/cesnet/folder/1001/file")).file(createFile()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.uuid', notNullValue()))
                .andExpect(jsonPath('$.filename').value(FILE_NAME))
                .andExpect(jsonPath('$.mimetype').value(MIME_TYPE))
                .andExpect(jsonPath('$.filesize').value(FILE_CONTENTS.getBytes().length))
    }

    @Test
    void "should return 404 error for non-existing folder"() {
        mockMvc.perform(fileUpload(fromApi("space/cesnet/folder/666/file")).file(createFile()))
                .andExpect(status().isNotFound())
    }

    @Test
    void "should return 404 error for non-existing space"() {
        mockMvc.perform(fileUpload(fromApi("space/xxx/folder/1001/file")).file(createFile()))
                .andExpect(status().isNotFound())
    }

    def createFile() {
        return new MockMultipartFile("file", FILE_NAME, MIME_TYPE, FILE_CONTENTS.getBytes())
    }

}
