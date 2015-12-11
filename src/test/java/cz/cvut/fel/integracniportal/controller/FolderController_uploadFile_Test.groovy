package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static org.hamcrest.CoreMatchers.notNullValue
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
public class FolderController_uploadFile_Test extends AbstractIntegrationTestCase {

    final FILE_NAME = "x.json"
    final MIME_TYPE = "application/json"
    final FILE_CONTENTS = "{}";

    @Test
    void "should upload a file and return meta data"() {

        mockMvc.perform(uploadFile("space/cesnet/folder/1001/file"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.uuid', notNullValue()))
                .andExpect(jsonPath('$.name').value(FILE_NAME))
                .andExpect(jsonPath('$.mimetype').value(MIME_TYPE))
                .andExpect(jsonPath('$.filesize').value(FILE_CONTENTS.getBytes().length))
    }

    @Test
    void "should return 404 error for non-existing folder"() {
        mockMvc.perform(uploadFile("space/cesnet/folder/666/file"))
                .andExpect(status().isNotFound())
    }

    @Test
    void "should return 404 error for non-existing space"() {
        mockMvc.perform(uploadFile("space/xxx/folder/1001/file"))
                .andExpect(status().isNotFound())
    }

    def uploadFile(url) {
        return upload(url, FILE_NAME, FILE_CONTENTS.getBytes(), MIME_TYPE)
    }

}
