package cz.cvut.fel.integracniportal.domain.user.saga;

import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordResetExpiredEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordResetStartedEvent;
import cz.cvut.fel.integracniportal.domain.user.events.UserPasswordSetEvent;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;


/**
 * @author Radek Jezdik
 */
public class RestoreLostUserPasswordSaga extends AbstractExpirableUserTokenSaga {

    @StartSaga
    @SagaEventHandler(associationProperty = "userId")
    public void handle(UserPasswordResetStartedEvent event) {
        createTokenAndScheduleExpireEvent(
                event.getUserId(),
                new UserPasswordResetExpiredEvent(event.getUserId()));
    }

    @SagaEventHandler(associationProperty = "userId")
    public void handle(UserPasswordSetEvent event) {
        finish(event.getUserId());
    }

    @SagaEventHandler(associationProperty = "userId")
    public void handle(UserPasswordResetExpiredEvent event) {
        finish(event.getUserId());
    }

}
