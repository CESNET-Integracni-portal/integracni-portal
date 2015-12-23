package cz.cvut.fel.integracniportal.dao;

import cz.cvut.fel.integracniportal.model.Node;

import java.io.Serializable;

/**
 * @author Eldar Iosip
 */
public interface NodeDao {

    Node get(Serializable id);

    public void save(Node node);

    void delete(Node node);
}
