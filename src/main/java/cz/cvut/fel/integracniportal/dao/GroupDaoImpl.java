package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QGroup.group;

/**
 * @author Radek Jezdik
 */
@Repository
public class GroupDaoImpl extends GenericHibernateDao<Group> implements GroupDao {

    public GroupDaoImpl() {
        super(Group.class);
    }

    @Override
    public List<Group> getUserGroups(UserDetails owner) {
        return from(group)
                .where(group.owner.eq(owner))
                .list(group);
    }

    @Override
    public boolean groupExists(String id, String name) {
        return from(group)
                .where(group.owner.userId.eq(id))
                .where(group.name.eq(name))
                .exists();
    }

}
