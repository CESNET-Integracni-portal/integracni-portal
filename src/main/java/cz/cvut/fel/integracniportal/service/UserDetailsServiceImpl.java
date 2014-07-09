package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.UserDetailsDao;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link cz.cvut.fel.integracniportal.service.UserDetailsService}.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDetailsDao userDao;

    @Override
    public UserDetails getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public UserDetails getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    @Override
    public void saveUser(UserDetails user) {
        userDao.saveUser(user);
    }

    @Override
    public void removeUser(UserDetails user) {
        userDao.removeUser(user);
    }
}
