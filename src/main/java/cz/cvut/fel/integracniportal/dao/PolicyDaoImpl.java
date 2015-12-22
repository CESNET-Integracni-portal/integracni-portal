package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AccessControlEntry;
import cz.cvut.fel.integracniportal.model.Policy;
import org.springframework.stereotype.Repository;

/**
 * @author Eldar Iosip
 */
@Repository
public class PolicyDaoImpl extends GenericHibernateDao<Policy> implements PolicyDao {

    public PolicyDaoImpl() {
        super(Policy.class);
    }
    
}
