package cz.cvut.fel.integracniportal.integration.group

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.group.CreateGroupCommand
import cz.cvut.fel.integracniportal.command.group.DeleteGroupCommand
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId
import cz.cvut.fel.integracniportal.model.Group

/**
 * Integration test for {@link DeleteGroupCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class DeleteGroupIntegrationSpec extends AbstractIntegrationSpecification {

    def id = "1"

    def "should delete a group"() {
        given:
            dispatch new CreateGroupCommand(GroupId.of(id), "group")

            assert get(Group, id) != null

        when:
            dispatch new DeleteGroupCommand(GroupId.of(id))

        then:
            get(Group, id) == null
    }

}
