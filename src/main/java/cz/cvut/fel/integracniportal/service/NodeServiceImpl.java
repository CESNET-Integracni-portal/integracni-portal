package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.NodeDao;
import cz.cvut.fel.integracniportal.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeDao nodeDao;

    @Override
    public Node getNodeById(Long nodeId) {
        return nodeDao.get(nodeId);
    }

    @Override
    public void saveNode(Node node) {
        nodeDao.save(node);
    }
}
