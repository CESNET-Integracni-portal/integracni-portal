package cz.cvut.fel.integracniportal.service;


import cz.cvut.fel.integracniportal.model.Node;
import cz.cvut.fel.integracniportal.representation.SharedNodeRepresentation;

/**
 * @author Eldar Iosip
 */
public interface NodeService {

    public Node getNodeById(Long nodeId);

    public SharedNodeRepresentation getSharedNodeRepresentation(String spaceId);

    public void saveNode(Node node);

}
