package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Node;
import org.springframework.stereotype.Component;

/**
 * @author Radek Jezdik
 */
@Component
public class NodeDaoImpl extends GenericHibernateDao<Node> {

    public NodeDaoImpl() {
        super(Node.class);
    }

}
