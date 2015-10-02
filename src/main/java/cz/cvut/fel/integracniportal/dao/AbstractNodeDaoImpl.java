package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.QAbstractNode;
import org.springframework.stereotype.Repository;

/**
 * @author Eldar Iosip
 */
@Repository
public class AbstractNodeDaoImpl extends GenericHibernateDao<AbstractNode> implements AbstractNodeDao {

    public AbstractNodeDaoImpl() {
        super(AbstractNode.class);
    }

    @Override
    public AbstractNode getAclParentFor(String nodeId) {
        return null;
        /*
        return from(abstractNode)
                .where(abstractNode.nodeId.eq(nodeId))
                .uniqueResult(abstractNode);
                */
    }

    @Override
    public void setAclParentFor(String nodeId) {

    }

    @Override
    public void save(AbstractNode abstractNode) {

    }

    @Override
    public void delete(AbstractNode abstractNode) {

    }
}
