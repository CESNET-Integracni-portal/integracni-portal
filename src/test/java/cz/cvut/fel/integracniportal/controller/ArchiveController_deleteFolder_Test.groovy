package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.dao.FolderDao
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import static org.junit.Assert.assertNull
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DatabaseSetup("fileMetadata.xml")
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArchiveController_deleteFolder_Test extends AbstractIntegrationTestCase {

    @Autowired
    FolderDao folderDao

    @Test
    void "should return the folder resource"() {
        apiDelete("archive/folder/2")
                .andExpect(status().isNoContent())

        def folder = folderDao.get(2L)

        assertNull folder
    }


    @Test
    void "should return 404 Not Found for non existing folder"() {
        apiDelete("archive/folder/666")
                .andExpect(status().isNotFound())
    }

}
