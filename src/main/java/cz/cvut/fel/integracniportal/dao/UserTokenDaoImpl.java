package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserToken;
import org.springframework.stereotype.Repository;

import static cz.cvut.fel.integracniportal.model.QUserToken.userToken;

/**
 * @author Radek Jezdik
 */
@Repository
public class UserTokenDaoImpl extends GenericHibernateDao<UserToken>
        implements UserTokenDao {

    public UserTokenDaoImpl() {
        super(UserToken.class);
    }

    @Override
    public void addToken(String userId, String token) {
        UserToken verification = new UserToken();
        verification.setId(userId);
        verification.setToken(token);
        save(verification);
    }

    @Override
    public String getUserIdByToken(String token) {
        return from(userToken)
                .where(userToken.token.eq(token))
                .singleResult(userToken.userId);
    }

    @Override
    public void deleteToken(String userId) {
        UserToken verification = load(userId);
        delete(verification);
    }

}
