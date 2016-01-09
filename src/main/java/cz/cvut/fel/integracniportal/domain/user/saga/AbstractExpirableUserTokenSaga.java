package cz.cvut.fel.integracniportal.domain.user.saga;

import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.dao.UserTokenDao;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.service.SecureRandomGenerator;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Radek Jezdik
 */
public abstract class AbstractExpirableUserTokenSaga extends AbstractAnnotatedSaga {

    private long expireInMinutes = 60;

    @Autowired
    protected transient UserDetailsDao userDao;

    @Autowired
    protected transient UserTokenDao userTokenDao;

    @Autowired
    protected transient EventScheduler scheduler;

    @Autowired
    protected transient SecureRandomGenerator randomGenerator;

    private ScheduleToken timeoutToken;

    protected String createTokenAndScheduleExpireEvent(UserId userId, Object expireEvent) {
        String token = randomGenerator.generate();

        userTokenDao.addToken(userId.getId(), token);

        timeoutToken = scheduler.schedule(
                getExpireDuration(),
                expireEvent);

        return token;
    }

    protected void finish(UserId userId) {
        userTokenDao.deleteToken(userId.getId());
        scheduler.cancelSchedule(timeoutToken);
        end();
    }

    private Duration getExpireDuration() {
        return Duration.standardMinutes(expireInMinutes);
    }

}
