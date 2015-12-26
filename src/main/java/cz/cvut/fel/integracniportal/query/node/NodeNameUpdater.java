package cz.cvut.fel.integracniportal.query.node;

import cz.cvut.fel.integracniportal.dao.NodeNameDao;
import cz.cvut.fel.integracniportal.domain.node.events.*;
import cz.cvut.fel.integracniportal.domain.user.valueobjects.UserId;
import cz.cvut.fel.integracniportal.model.NodeName;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Radek Jezdik
 */
@Component
public class NodeNameUpdater {

    @Autowired
    private NodeNameDao nodeNameDao;

    @EventHandler
    public void handle(NodeCreatedEvent event) {
        createNodeName(event);
    }

    @EventHandler
    public void handle(NodeRenamedEvent event) {
        renameNodeName(event);
    }

    @EventHandler
    public void handle(NodeMovedEvent event) {
        moveNodeName(event);
    }

    @EventHandler
    public void moveFileToRoot(FileMovedToRootEvent event) {
        moveToRoot(event, event.getRootOwner());
    }

    @EventHandler
    public void moveFolderToRoot(FolderMovedToRootEvent event) {
        moveToRoot(event, event.getRootOwner());
    }

    @EventHandler
    public void handle(NodeDeletedEvent event) {
        deleteNodeName(event);
    }

    private void createNodeName(NodeCreatedEvent event) {
        NodeName nodeName = new NodeName();
        nodeName.setId(event.getId().getId());
        nodeName.setName(event.getName());
        nodeName.setSpace(event.getSpace());
        nodeName.setIsFolder(event instanceof FolderCreatedEvent);

        if (event.getParentFolder() == null) {
            nodeName.setUserId(event.getOwner().getId());
        } else {
            nodeName.setParentId(event.getParentFolder().getId());
        }

        nodeNameDao.save(nodeName);
    }

    private void renameNodeName(NodeRenamedEvent event) {
        NodeName nodeName = nodeNameDao.load(event.getId().getId());

        nodeName.setName(event.getNewName());
    }

    private void moveNodeName(NodeMovedEvent event) {
        NodeName nodeName = nodeNameDao.load(event.getId().getId());

        nodeName.setParentId(event.getNewParent().getId());
    }

    private void moveToRoot(NodeMovedEvent event, UserId rootOwner) {
        NodeName nodeName = nodeNameDao.load(event.getId().getId());

        nodeName.setParentId(null);
        nodeName.setUserId(rootOwner.getId());
    }

    private void deleteNodeName(NodeDeletedEvent event) {
        NodeName nodeName = nodeNameDao.load(event.getId().getId());
        nodeNameDao.delete(nodeName);
    }

}
