package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.AbstractNode;
import cz.cvut.fel.integracniportal.model.QAbstractNode;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static cz.cvut.fel.integracniportal.model.QAbstractNode.abstractNode;

/**
 * @author Eldar Iosip
 */
@Repository
public class AbstractNodeDaoImpl extends GenericHibernateDao<AbstractNode> implements AbstractNodeDao {

    public AbstractNodeDaoImpl() {
        super(AbstractNode.class);
    }

    @Override
    public AbstractNode getById(String nodeId) {
        return from(abstractNode)
                .where(abstractNode.nodeId.eq(nodeId))
                .uniqueResult(abstractNode);
    }

    @Override
    public void update(AbstractNode abstractNode) {
        abstractNode.setChangedOn(new Date());
        super.update(abstractNode);
    }
}
