package cz.cvut.fel.integracniportal.service;


import cz.cvut.fel.integracniportal.model.Node;

/**
 * @author Eldar Iosip
 */
public interface NodeService {

    public Node getNodeById(Long nodeId);

    public void saveNode(Node node);

}
