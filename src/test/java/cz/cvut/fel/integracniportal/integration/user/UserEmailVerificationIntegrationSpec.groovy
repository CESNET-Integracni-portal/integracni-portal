package cz.cvut.fel.integracniportal.integration.user

import cz.cvut.fel.integracniportal.AbstractIntegrationSpecification
import cz.cvut.fel.integracniportal.command.user.CreateUserCommand
import cz.cvut.fel.integracniportal.command.user.SetUserPasswordCommand
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId
import cz.cvut.fel.integracniportal.exceptions.NotFoundException
import cz.cvut.fel.integracniportal.model.UserToken

/**
 * Integration test for {@link CreateUserCommand}.
 *
 * @author Radek Jezdik
 */
public class UserEmailVerificationIntegrationSpec extends AbstractIntegrationSpecification {

    def "should verify user email by token and set user password"() {
        given:
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com")

        when:
            dispatch new SetUserPasswordCommand(get(UserToken, "1").token, password("newPassword"))

        then:
            def user = getUser("1")
            user.password == "newPassword"
    }

    def "should throw exception if unknown token was passed"() {
        given:
            dispatch new CreateUserCommand(UserId.of("1"), "tester1", "test@example.com")

        when:
            def unknownToken = ";@;666;@;"
            dispatch new SetUserPasswordCommand(unknownToken, password("newPassword"))

        then:
            def user = getUser("1")
            user.password != "newPassword"

            thrown NotFoundException
    }

}
