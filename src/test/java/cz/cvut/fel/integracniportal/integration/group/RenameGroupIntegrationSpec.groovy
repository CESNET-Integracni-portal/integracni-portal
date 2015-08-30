package cz.cvut.fel.integracniportal.integration.group

import com.github.springtestdbunit.annotation.DatabaseSetup
import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.group.CreateGroupCommand
import cz.cvut.fel.integracniportal.command.group.RenameGroupCommand
import cz.cvut.fel.integracniportal.domain.group.valueobjects.GroupId
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException
import cz.cvut.fel.integracniportal.model.Group

/**
 * Integration test for {@link RenameGroupCommand}.
 *
 * @author Radek Jezdik
 */
@DatabaseSetup("classpath:user.xml")
public class RenameGroupIntegrationSpec extends AbstractIntegrationSpecification {

    def "should rename a group"() {
        given:
            dispatch new CreateGroupCommand(GroupId.of("1"), "group")

        when:
            dispatch new RenameGroupCommand(GroupId.of("1"), "renamedGroup")

        then:
            def group = get(Group, "1")
            group.id == "1"
            group.name == "renamedGroup"
            group.getOwner().getId() == 1
    }

    def "renaming group resulting in duplicate name throws exception"() {
        given:
            dispatch new CreateGroupCommand(GroupId.of("1"), "foo")
            dispatch new CreateGroupCommand(GroupId.of("2"), "bar")

        when:
            dispatch new RenameGroupCommand(GroupId.of("1"), "bar")

        then:
            thrown(AlreadyExistsException)
    }

}
