package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.model.NodeName;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cz.cvut.fel.integracniportal.model.QNodeName.nodeName;

/**
 * Hibernate implementation of the FileMetadataDao interface.
 */
@Repository
public class NodeNameDaoImpl extends GenericHibernateDao<NodeName> implements NodeNameDao {

    public NodeNameDaoImpl() {
        super(NodeName.class);
    }

    @Override
    public boolean nameExists(String name, FolderId parentId) {
        return from(nodeName)
                .where(nodeName.name.eq(name))
                .where(nodeName.parentId.eq(parentId.getId()))
                .exists();
    }

    @Override
    public boolean nameInRootExists(String name, UserId userId, String space) {
        return from(nodeName)
                .where(nodeName.name.eq(name))
                .where(nodeName.userId.eq(userId.getId()))
                .where(nodeName.space.eq(space))
                .exists();
    }

    @Override
    public List<NodeName> getChildNodes(FolderId folderId) {
        return from(nodeName)
                .where(nodeName.parentId.eq(folderId.getId()))
                .list(nodeName);
    }
}
