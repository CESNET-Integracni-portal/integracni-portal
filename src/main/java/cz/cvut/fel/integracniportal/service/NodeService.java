package cz.cvut.fel.integracniportal.service;


import cz.cvut.fel.integracniportal.model.Node;
import cz.cvut.fel.integracniportal.representation.SharedNodeRepresentation;

/**
 * @author Eldar Iosip
 */
public interface NodeService {

    Node getNodeById(Long nodeId);

    /**
     * Return a list of files and folders shared via ACL.
     *
     * @param spaceId string
     * @return SharedNodeRepresentation
     */
    SharedNodeRepresentation getSharedNodeRepresentation(String spaceId);

    void saveNode(Node node);

    void removeNode(Node node, boolean removeFromRepository);

}
