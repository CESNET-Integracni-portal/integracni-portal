package cz.cvut.fel.integracniportal.integration.group

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.group.CreateGroupCommand
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException
import cz.cvut.fel.integracniportal.model.Group

/**
 * Integration test for {@link CreateGroupCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class CreateGroupIntegrationSpec extends AbstractIntegrationSpecification {

    def "should create a group"() {
        when:
            dispatch new CreateGroupCommand(GroupId.of("1"), "group")

        then:
            def group = get(Group, "1")
            group.id == "1"
            group.name == "group"
            group.getOwner().getId() == 1
    }

    def "creating duplicate group name results in error"() {
        when:
            dispatch new CreateGroupCommand(GroupId.of("1"), "group")
            dispatch new CreateGroupCommand(GroupId.of("2"), "group")

        then:
            thrown(AlreadyExistsException)
    }

}
