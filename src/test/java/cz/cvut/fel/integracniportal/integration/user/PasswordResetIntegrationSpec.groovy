package cz.cvut.fel.integracniportal.integration.user

import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.user.CreateUserCommand
import cz.cvut.fel.integracniportal.command.user.ResetUserPasswordCommand
import cz.cvut.fel.integracniportal.command.user.SetUserPasswordCommand
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.exceptions.NotFoundException
import cz.cvut.fel.integracniportal.model.UserToken

/**
 * Integration test for {@link CreateUserCommand}.
 *
 * @author Radek Jezdik
 */
public class PasswordResetIntegrationSpec extends AbstractIntegrationSpecification {

    def "should reset user password by token if password was forgotten"() {
        given:
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com")
            dispatch new SetUserPasswordCommand(get(UserToken, "1").token, password("foo"))

            def user = getUser("1")
            assert user.password == "foo"

        when:
            dispatch new ResetUserPasswordCommand(UserId.of("1"))
            dispatch new SetUserPasswordCommand(get(UserToken, "1").token, password("bar"))

        then:
            def user2 = getUser("1")
            user2.password == "bar"
    }

    def "should throw exception if unknown token was passed"() {
        given:
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com")
            dispatch new SetUserPasswordCommand(get(UserToken, "1").token, password("initialPassword"))

            assert getUser("1").password == "initialPassword"

        when:
            dispatch new ResetUserPasswordCommand(UserId.of("1"))

            def unknownToken = ";@;666;@;"
            dispatch new SetUserPasswordCommand(unknownToken, password("newPassword"))

        then:
            def user = getUser("1")
            user.password == "initialPassword"

            thrown NotFoundException
    }

}
