package cz.cvut.fel.integracniportal.integration.group

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.group.AddUserToGroupCommand
import cz.cvut.fel.integracniportal.command.group.CreateGroupCommand
import cz.cvut.fel.integracniportal.command.group.RemoveUserFromGroupCommand
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.model.Group

/**
 * Integration test for {@link RemoveUserFromGroupCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class RemoveUserFromGroupIntegrationSpec extends AbstractIntegrationSpecification {

    def "should remove a user from a group"() {
        given:
            dispatch new CreateGroupCommand(GroupId.of("1"), "foo")

            dispatch new AddUserToGroupCommand(GroupId.of("1"), UserId.of("1"))
            dispatch new AddUserToGroupCommand(GroupId.of("1"), UserId.of("2"))

            assert get(Group, "1").getMembers().size() == 2

        when:
            dispatch new RemoveUserFromGroupCommand(GroupId.of("1"), UserId.of("1"))

        then:
            def members = get(Group, "1").getMembers()
            members.size() == 1
            members.contains(getUser("2"))
    }

    def "removing a user that was not added to a group does nothing"() {
        given:
            dispatch new CreateGroupCommand(GroupId.of("1"), "foo")
            dispatch new AddUserToGroupCommand(GroupId.of("1"), UserId.of("1"))

            assert get(Group, "1").getMembers().size() == 1

        when:
            dispatch new RemoveUserFromGroupCommand(GroupId.of("1"), UserId.of("666"))

        then:
            def members = get(Group, "1").getMembers()
            members.size() == 1
            members.contains(getUser("1"))
    }

}
