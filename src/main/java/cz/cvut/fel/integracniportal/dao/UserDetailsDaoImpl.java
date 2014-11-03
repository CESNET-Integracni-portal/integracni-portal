package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserDetails;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hibernate implementation of the UserDetailsDao interface.
 */
@Repository
public class UserDetailsDaoImpl extends HibernateDao implements UserDetailsDao {

    @Override
    @Transactional(readOnly = true)
    public UserDetails getUserById(long userId) {
        return getHibernateTemplate().get(UserDetails.class, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails getUserByUsername(String username) {
        Criteria criteria = getCriteria(UserDetails.class, "user");
        criteria.add(Restrictions.eq("user.username", username));
        return (UserDetails) criteria.uniqueResult();
    }

    @Override
    public List<UserDetails> getAllUsers() {
        Criteria criteria = getCriteria(UserDetails.class, "user");
        return criteria.list();
    }

    @Override
    @Transactional
    public void saveUser(UserDetails user) {
        getHibernateTemplate().saveOrUpdate(user);
    }

    @Override
    @Transactional
    public void removeUser(UserDetails user) {
        getHibernateTemplate().delete(user);
    }
}
