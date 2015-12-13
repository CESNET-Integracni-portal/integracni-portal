package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Node;
import org.springframework.stereotype.Repository;

/**
 * @author Eldar Iosip
 */
@Repository
public class NodeDaoImpl extends GenericHibernateDao<Node> implements NodeDao {

    public NodeDaoImpl() {
        super(Node.class);
    }
}
