package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.model.AbstractNode;

/**
 * @author Eldar Iosip
 */
public interface AbstractNodeService {

    public AbstractNode getById(String nodeId);

    public void saveAbstractNode(AbstractNode abstractNode);
}
