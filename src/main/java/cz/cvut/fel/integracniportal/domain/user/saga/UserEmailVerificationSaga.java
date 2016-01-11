package cz.cvut.fel.integracniportal.domain.user.saga;

import cz.cvut.fel.integracniportal.domain.user.events.UserEmailVerificationStartedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserEmailVerifiedEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;


/**
 * @author Radek Jezdik
 */
public class UserEmailVerificationSaga extends AbstractExpirableUserTokenSaga {

    @StartSaga
    @SagaEventHandler(keyName = "userId", associationProperty = "userId")
    public void handle(UserEmailVerificationStartedEvent event) {
        String token = createTokenAndScheduleExpireEvent(event.getUserId());

        // TODO send token via email
//        UserDetails user = userDao.getUserById(event.getUserId().getId());
//        user.getEmail();
    }

    @SagaEventHandler(keyName = "userId", associationProperty = "userId")
    public void handle(UserEmailVerifiedEvent event) {
        end();
    }

}
