package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.model.NodeName;

import java.io.Serializable;

/**
 * Data Access Object interface for node names.
 */
public interface NodeNameDao {

    public NodeName load(Serializable id);

    public NodeName get(Serializable id);

    public void save(NodeName nodeName);

    public void delete(NodeName nodeName);

    public boolean nameExists(String name, FolderId parentId);

    public boolean nameInRootExists(String name, UserId userId, String space);
}
