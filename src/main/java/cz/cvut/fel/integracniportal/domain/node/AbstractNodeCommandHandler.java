package cz.cvut.fel.integracniportal.domain.node;

import cz.cvut.fel.integracniportal.command.node.UserAwareCommand;
import cz.cvut.fel.integracniportal.dao.NodeNameDao;
import cz.cvut.fel.integracniportal.domain.node.valueobjects.FolderId;
import cz.cvut.fel.integracniportal.exceptions.DuplicateNameException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Radek Jezdik
 */
public abstract class AbstractNodeCommandHandler {

    @Autowired
    private NodeNameDao nodeNameDao;

    protected void checkUniqueName(String name, FolderId parentFolder, AbstractNodeAggregateRoot folder, UserAwareCommand command) {
        boolean exists;

        if (parentFolder == null) {
            exists = nodeNameDao.nameInRootExists(name, command.getSentBy(), folder.getSpace());
        } else {
            exists = nodeNameDao.nameExists(name, parentFolder);
        }

        if (exists) {
            throw new DuplicateNameException();
        }
    }

}
