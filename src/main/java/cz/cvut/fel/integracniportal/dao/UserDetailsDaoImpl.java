package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QUserDetails.userDetails;

/**
 * Hibernate implementation of the UserDetailsDao interface.
 */
@Repository
public class UserDetailsDaoImpl extends GenericHibernateDao<UserDetails> implements UserDetailsDao {

    public UserDetailsDaoImpl() {
        super(UserDetails.class);
    }

    @Override
    public UserDetails getReference(String userId) {
        return load(userId);
    }

    @Override
    public UserDetails getUserById(String userId) {
        return get(userId);
    }

    @Override
    public UserDetails getUserByUsername(String username) {
        return from(userDetails)
                .where(userDetails.username.eq(username))
                .uniqueResult(userDetails);
    }

    @Override
    public List<UserDetails> getAllUsers() {
        return from(userDetails)
                .list(userDetails);
    }

    @Override
    public List<UserDetails> getAllUsersInOrganizationalUnit(String organizationalUnitId) {
        return from(userDetails)
                .where(userDetails.organizationalUnit.unitId.eq(organizationalUnitId))
                .list(userDetails);
    }

    @Override
    public boolean usernameExists(String username) {
        return from(userDetails)
                .where(userDetails.username.eq(username))
                .exists();
    }

    @Override
    public boolean emailExists(String email) {
        return from(userDetails)
                .where(userDetails.email.eq(email))
                .exists();
    }

}
