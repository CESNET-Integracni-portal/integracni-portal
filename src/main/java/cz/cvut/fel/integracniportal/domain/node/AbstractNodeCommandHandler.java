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
    protected NodeNameDao nodeNameDao;

    /**
     * Checks whether the name of the node is unique, in the given parent folder in user's space
     * @param name the new name of the node
     * @param parentFolder the parent folder where to check for unique name
     * @param space the space of the folder where it belongs
     * @param command the UserAwareCommand which holds the user who sent the command
     */
    protected void checkUniqueName(String name, FolderId parentFolder, String space, UserAwareCommand command) {
        boolean exists;

        if (parentFolder == null) {
            exists = nodeNameDao.nameInRootExists(name, command.getSentBy(), space);
        } else {
            exists = nodeNameDao.nameExists(name, parentFolder);
        }

        if (exists) {
            throw new DuplicateNameException();
        }
    }

}
