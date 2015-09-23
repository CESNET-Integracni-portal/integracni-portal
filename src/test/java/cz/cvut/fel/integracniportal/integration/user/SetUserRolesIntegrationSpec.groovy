package cz.cvut.fel.integracniportal.integration.user

import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.user.CreateUserCommand
import cz.cvut.fel.integracniportal.command.user.SetUserRolesCommand
import cz.cvut.fel.integracniportal.command.userrole.CreateUserRoleCommand
import cz.cvut.fel.integracniportal.domain.Permission
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId

/**
 * Integration test for {@link CreateUserCommand}.
 *§ů
 * @author Radek Jezdik
 */
public class SetUserRolesIntegrationSpec extends AbstractIntegrationSpecification {

    def "should add roles to existing user"() {
        given:
            dispatch new CreateUserRoleCommand(UserRoleId.of("1"), "foo", "", [Permission.CHANGE_PASSWORD] as Set)
            dispatch new CreateUserRoleCommand(UserRoleId.of("2"), "bar", "", [Permission.EDIT_EXTERNISTS] as Set)

            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com", [UserRoleId.of("1")] as Set)
            assert getUser("1").userRoles.size() == 1

        when:
            def roles = [
                    UserRoleId.of("1"),
                    UserRoleId.of("2")
            ] as Set
            dispatch new SetUserRolesCommand(UserId.of("1"), roles)

        then:
            def user = getUser("1")
            user.userRoles.size() == 2
    }

    def "should remove role from existing user"() {
        given:
            dispatch new CreateUserRoleCommand(UserRoleId.of("1"), "foo", "", [Permission.CHANGE_PASSWORD] as Set)
            dispatch new CreateUserRoleCommand(UserRoleId.of("2"), "bar", "", [Permission.EDIT_EXTERNISTS] as Set)

            def userRoles = [
                    UserRoleId.of("1"),
                    UserRoleId.of("2")
            ] as Set

            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com", userRoles)

            assert getUser("1").userRoles.size() == 2

        when:
            def updateRoles = [
                    UserRoleId.of("1")
            ] as Set
            dispatch new SetUserRolesCommand(UserId.of("1"), updateRoles)

        then:
            def user = getUser("1")
            user.userRoles.size() == 1
            user.userRoles.first().getId() == "1"
    }

    def "should both add and remove role from existing user"() {
        given:
            dispatch new CreateUserRoleCommand(UserRoleId.of("1"), "foo", "", [Permission.CHANGE_PASSWORD] as Set)
            dispatch new CreateUserRoleCommand(UserRoleId.of("2"), "bar", "", [Permission.EDIT_EXTERNISTS] as Set)
            dispatch new CreateUserRoleCommand(UserRoleId.of("3"), "baz", "", [Permission.EDIT_EXTERNISTS] as Set)

            def userRoles = [
                    UserRoleId.of("1"),
                    UserRoleId.of("2")
            ] as Set

            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com", userRoles)

            assert getUser("1").userRoles.size() == 2

        when:
            def updateRoles = [
                    UserRoleId.of("1"),
                    UserRoleId.of("3")
            ] as Set
            dispatch new SetUserRolesCommand(UserId.of("1"), updateRoles)

        then:
            def user = getUser("1")
            user.userRoles.size() == 2

            def roleIds = user.userRoles.collect { it.id }
            roleIds.contains("1")
            roleIds.contains("3")
    }

}
