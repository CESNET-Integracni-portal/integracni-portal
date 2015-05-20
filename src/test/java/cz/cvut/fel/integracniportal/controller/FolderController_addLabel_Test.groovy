package cz.cvut.fel.integracniportal.controller

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationTestCase
import cz.cvut.fel.integracniportal.SpringockitoWebContextLoader
import cz.cvut.fel.integracniportal.representation.LabelIdRepresentation
import cz.cvut.fel.integracniportal.representation.LabelRepresentation
import cz.cvut.fel.integracniportal.representation.UserDetailsRepresentation
import cz.cvut.fel.integracniportal.service.FolderService
import cz.cvut.fel.integracniportal.service.LabelService
import cz.cvut.fel.integracniportal.service.UserDetailsService
import org.junit.Test
import org.kubek2k.springockito.annotations.experimental.DirtiesMocks
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize
import static junit.framework.Assert.assertEquals
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Test for {@link FolderController#createSubFolder(java.lang.String, java.lang.Long, cz.cvut.fel.integracniportal.representation.NameRepresentation)}
 *
 * @author Radek Jezdik
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class)
@DirtiesMocks(classMode = DirtiesMocks.ClassMode.AFTER_EACH_TEST_METHOD)
@DatabaseSetup("classpath:fileMetadata.xml")
@Transactional
public class FolderController_addLabel_Test extends AbstractIntegrationTestCase {

    @Autowired
    private LabelService labelService

    @Autowired
    private FolderService folderService

    @Autowired
    private UserDetailsService userService

    @Test
    void "should assign labels to a folder"() {
        def label1 = labelService.createLabel(new LabelRepresentation(name: "work", owner: 0, color: "red"))
        def label2 = labelService.createLabel(new LabelRepresentation(name: "cesnet", owner: 0, color: "blue"))

        apiPost("space/cesnet/folder/1001/addLabel", '{"labelId": ' + label1.getId() + '}')
                .andExpect(status().isNoContent())

        apiPost("space/cesnet/folder/1002/addLabel", '{"labelId": ' + label1.getId() + '}')
                .andExpect(status().isNoContent())
        apiPost("space/cesnet/folder/1002/addLabel", '{"labelId": ' + label2.getId() + '}')
                .andExpect(status().isNoContent())

        assertEquals 1, folderService.getFolderRepresentationById(1001, userService.getCurrentUser()).getLabels().size()
        assertEquals 2, folderService.getFolderRepresentationById(1002, userService.getCurrentUser()).getLabels().size()
    }

    @Test
    void "assigning labels of two users should retrieve both for one user"() {
        def user = userService.createUser(new UserDetailsRepresentation(username: "user", password: "xyz"))

        def label1 = labelService.createLabel(new LabelRepresentation(name: "work", owner: 0, color: "red"))
        def label2 = labelService.createLabel(new LabelRepresentation(name: "cesnet", owner: user.getId(), color: "blue"))

        labelService.addLabelToFolder(1001, new LabelIdRepresentation(labelId: label1.getId()), userService.getCurrentUser())
        labelService.addLabelToFolder(1001, new LabelIdRepresentation(labelId: label2.getId()), user)

        apiGet("space/cesnet/folder/1001")
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.labels', hasSize(1)))

    }

    @Test
    void "should return 404 error for non-existing label"() {
        apiPost("space/cesnet/file/1002/addLabel", '{"labelId": 666}')
                .andExpect(status().isNotFound())
    }

    @Test
    void "should return 400 error for label thats not users"() {
        def user = userService.createUser(new UserDetailsRepresentation(username: "user", password: "xyz"))

        def label = labelService.createLabel(new LabelRepresentation(name: "work", owner: user.getId(), color: "red"))

        apiPost("space/cesnet/file/1002/addLabel", '{"labelId": ' + label.getId() + '}')
                .andExpect(status().isBadRequest())
    }

}
