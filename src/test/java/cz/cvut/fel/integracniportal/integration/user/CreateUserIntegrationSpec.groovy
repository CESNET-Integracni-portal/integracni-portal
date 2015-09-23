package cz.cvut.fel.integracniportal.integration.user

import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.user.CreateUserCommand
import cz.cvut.fel.integracniportal.command.userrole.CreateUserRoleCommand
import cz.cvut.fel.integracniportal.domain.Permission
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.domain.userrole.valueobjects.UserRoleId
import cz.cvut.fel.integracniportal.exceptions.AlreadyExistsException

/**
 * Integration test for {@link CreateUserCommand}.
 *
 * @author Radek Jezdik
 */
public class CreateUserIntegrationSpec extends AbstractIntegrationSpecification {

    def "should create a user"() {
        when:
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com")

        then:
            def user = getUser("1")
            user.username == "tester1"
            user.email == "test@example.com"
            user.userRoles.isEmpty()
    }

    def "should create a user with roles"() {
        given:
            dispatch new CreateUserRoleCommand(UserRoleId.of("1"), "foo", "", [Permission.CHANGE_PASSWORD] as Set)
            dispatch new CreateUserRoleCommand(UserRoleId.of("2"), "bar", "", [Permission.EDIT_EXTERNISTS] as Set)

        when:
            def roles = [
                    UserRoleId.of("1"),
                    UserRoleId.of("2")
            ] as Set
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com", roles)

        then:
            def user = getUser("1")
            user.username == "tester1"
            user.email == "test@example.com"
            user.userRoles.size() == 2
    }

    def "creating user with duplicate username results in error"() {
        given:
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com")
        when:
            dispatch new CreateUserCommand(UserId.of("2"), "tester1", "foo@bar.com")
        then:
            thrown(AlreadyExistsException)
    }

    def "creating user with duplicate email results in error"() {
        given:
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com")
        when:
            dispatch new CreateUserCommand(UserId.of("2"), "foo2", "test@example.com")
        then:
            thrown(AlreadyExistsException)
    }

}
