package cz.cvut.fel.integracniportal.dao;

/**
 * @author Radek Jezdik
 */
public interface UserTokenDao {

    public void addToken(String userId, String token);

    public String getUserIdByToken(String token);

    public void deleteToken(String userId);

}
