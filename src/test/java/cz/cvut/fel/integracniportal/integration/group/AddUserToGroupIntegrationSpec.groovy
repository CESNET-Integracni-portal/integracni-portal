package cz.cvut.fel.integracniportal.integration.group

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.group.AddUserToGroupCommand
import cz.cvut.fel.integracniportal.command.group.CreateGroupCommand
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.model.Group

/**
 * Integration test for {@link AddUserToGroupCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class AddUserToGroupIntegrationSpec extends AbstractIntegrationSpecification {

    def "should add a user as member to a group"() {
        given:
            dispatch new CreateGroupCommand(GroupId.of("1"), "foo")
            dispatch new CreateGroupCommand(GroupId.of("2"), "bar")

        when:
            dispatch new AddUserToGroupCommand(GroupId.of("1"), UserId.of(1))
            dispatch new AddUserToGroupCommand(GroupId.of("1"), UserId.of(2))

            dispatch new AddUserToGroupCommand(GroupId.of("2"), UserId.of(1))

        then:
            def members1 = get(Group, "1").getMembers()
            members1.size() == 2
            members1.contains(getUser(1))
            members1.contains(getUser(2))

            def members2 = get(Group, "2").getMembers()
            members2.size() == 1
            members2.contains(getUser(1))
    }

    def "adding the same user to group does nothing"() {
        given:
            dispatch new CreateGroupCommand(GroupId.of("1"), "foo")

        when:
            dispatch new AddUserToGroupCommand(GroupId.of("1"), UserId.of(1))
            dispatch new AddUserToGroupCommand(GroupId.of("1"), UserId.of(1))

        then:
            def members1 = get(Group, "1").getMembers()
            members1.size() == 1
            members1.contains(getUser(1))
    }

}
