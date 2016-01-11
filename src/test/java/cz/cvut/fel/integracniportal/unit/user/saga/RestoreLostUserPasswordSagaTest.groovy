package cz.cvut.fel.integracniportal.unit.user.saga

import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordResetStartedEvent
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordSetEvent
import cz.cvut.fel.integracniportal.domain.user.saga.RestoreLostUserPasswordSaga

/**
 * @author Radek Jezdik
 */
class RestoreLostUserPasswordSagaTest extends AbstractExpirableUserTokenSagaTest {

    def Class getSagaClass() {
        return RestoreLostUserPasswordSaga
    }

    def getStartEvent() {
        return new UserPasswordResetStartedEvent(user)
    }

    def getEndEvent() {
        return new UserPasswordSetEvent(user)
    }

}
