package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.AbstractUser;
import cz.cvut.fel.integracniportal.model.AclPermission;
import cz.cvut.fel.integracniportal.model.FileMetadata;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Eldar Iosip
 */
@Repository
public class AclPermissionDaoImpl extends GenericHibernateDao<AclPermission> implements AclPermissionDao {

    public AclPermissionDaoImpl() {
        super(AclPermission.class);
    }

    @Override
    public Set<AclPermission> getPermissionsForUserNode(AbstractUser user, AbstractNode node) {
        return null;
        /*
        return from(aclPermission)
                .where(aclPermission.targetUser.eq(user));
                */
    }

    @Override
    public void setPermissionsForUserNode(AbstractUser user, AbstractNode node, Set<AclPermission> permissions) {

    }

    @Override
    public void save(AclPermission permission) {

    }

    @Override
    public void delete(AclPermission permission) {

    }
}
