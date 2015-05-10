package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Group;
import cz.cvut.fel.integracniportal.model.UserDetails;

import java.io.Serializable;
import java.util.List;

/**
 * @author Radek Jezdik
 */
public interface GroupDao {

    public Group get(Serializable id);

    public List<Group> getUserGroups(UserDetails owner);

    public void save(Group group);

    public void delete(Group group);

}
