package cz.cvut.fel.integracniportal.unit.user.saga

import cz.cvut.fel.integracniportal.domain.user.events.UserEmailVerificationStartedEvent
import cz.cvut.fel.integracniportal.domain.user.events.UserEmailVerifiedEvent
import cz.cvut.fel.integracniportal.domain.user.saga.UserEmailVerificationSaga

/**
 * @author Radek Jezdik
 */
class UserEmailVerificationSagaTest extends AbstractExpirableUserTokenSagaTest {

    @Override
    def Class getSagaClass() {
        return UserEmailVerificationSaga
    }

    def getStartEvent() {
        return new UserEmailVerificationStartedEvent(user)
    }

    def getEndEvent() {
        return new UserEmailVerifiedEvent(user)
    }

}
