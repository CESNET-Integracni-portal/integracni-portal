package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AccessControlEntry;
import cz.cvut.fel.integracniportal.model.AccessControlPermission;
import cz.cvut.fel.integracniportal.model.Policy;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Eldar Iosip
 */
public interface PolicyDao {

    Policy get(Serializable id);

    void save(Policy policy);

    void delete(Policy policy);

    List<Policy> findByActiveAfter(Date date);

}