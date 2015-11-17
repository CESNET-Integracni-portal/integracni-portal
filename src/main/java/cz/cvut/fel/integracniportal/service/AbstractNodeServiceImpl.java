package cz.cvut.fel.integracniportal.service;

import cz.cvut.fel.integracniportal.dao.AbstractNodeDao;
import cz.cvut.fel.integracniportal.model.AbstractNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Eldar Iosip
 */
@Service
@Transactional
public class AbstractNodeServiceImpl implements AbstractNodeService {

    @Autowired
    private AbstractNodeDao abstractNodeDao;

    @Override
    public AbstractNode getById(String nodeId) {
        return abstractNodeDao.getById(nodeId);
    }
}
