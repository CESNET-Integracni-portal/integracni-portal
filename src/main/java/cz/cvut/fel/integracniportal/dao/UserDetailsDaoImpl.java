package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QUserDetails.userDetails;

/**
 * Hibernate implementation of the UserDetailsDao interface.
 */
@Repository
@Transactional
public class UserDetailsDaoImpl extends GenericHibernateDao<UserDetails> implements UserDetailsDao {

    public UserDetailsDaoImpl() {
        super(UserDetails.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails getUserById(long userId) {
        return get(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails getUserByUsername(String username) {
        return from(userDetails)
                .where(userDetails.username.eq(username))
                .uniqueResult(userDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetails> getAllUsers() {
        return from(userDetails)
                .list(userDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDetails> getAllUsersInOrganizationalUnit(Long organizationalUnitId) {
        return from(userDetails)
                .where(userDetails.organizationalUnitId.eq(organizationalUnitId))
                .list(userDetails);
    }

}
